window.addEventListener("load", init, true);
var text;
var request;
var socket;
var sseSource;
var bereitZumSenden = false;
function init() {
	// AJAX
	request = new XMLHttpRequest();
	request.onreadystatechange = getCatalogs;
	request.open("GET", "AJAX", true);
	request.send(null);
	// Websockets
	var url = 'ws://localhost:8080/Aufgabe5/Login';
	socket = new WebSocket(url);
	socket.onopen = sendenMoeglich;
	socket.onmessage = empfange;
	socket.onerror = error;
	socket.onclose = error;
	// SSE
	sseSource = new EventSource('SSEServlet');
	sseSource.addEventListener('LST', showPlayerListSSE, false);
	// Standard Events
	sseSource.addEventListener('error', errorSSE, false);
	// login
	document.getElementById("loginbutton").addEventListener("click", send);
}
function errorSSE(event){
//	if(event.eventPhase == EventSource.CLOSED) {  
//		alert("Error: Connection Closed"); }
//	else{
//		alert("Error: SSE");
//	}
	sseSource.close();
}
function error() {
	alert("Ein Serverseitiger Fehler wurde festgestellt. Sie wurden disconnected");
}

function getCatalogs() {
	switch (request.readyState) {
	case 4:
		var catalogName = request.responseXML.getElementsByTagName("catalog");
		var tables = "";
		for (var i = 0; i < catalogName.length; i++) {
			tables = tables + "<tr><td id=\"cata" + i + "\">"
					+ catalogName[i].firstChild.nodeValue + "</td></tr>";

		}
		var catalogTable = document.getElementById("cat");
		catalogTable.innerHTML = tables;
		for (var i = 0; i < catalogName.length; i++) {
			var catalogElement = document.getElementById("cata" + (i));
			catalogElement.addEventListener("click", changeColor, false);
		}
		break;
	default:
		break;
	}
}
function empfange(message) {
	var data = message.data;
	var msg = JSON.parse(data);
	console.log(msg['type']);
	switch (msg['type']) {
	case "CCH": // Catalog Change
		setCatalog(msg);
		break;
	case "LST": // Player List
		showPlayerList(msg);
		break;
	case "LOK": // Login OK
		deactivateLogin(msg);
		break;
	case "STG": // Start Game
		removeCatalogEventListener();
		questionRequest();
		break;
	case "QRE": // Question Recieved
		showQuestion(msg);
		break;
	case "QUR": // Question Result
		questionResult(msg);
		break;
	case "WOP": // Wait for all Player to finish
		waitPlayer(msg);
		break;
	case "ERR": // Error
		errorHandle(msg);
		break;
	default:
		break;
	}
}
function removeCatalogEventListener() {
	var rows = document.getElementById("cat").rows.length;
	for (var i = 0; i < rows; i++) {
		var row = document.getElementById("cata" + i);
		row.removeEventListener("click", changeColor, false);
	}
}
function send(event) {
	if (bereitZumSenden) {
		var username = document.getElementById("username").value;
		var json = "{\"type\":\"LRQ\", \"name\":\"" + username + "\"}";
		socket.send(json);
	}
}
function sendenMoeglich() {
	bereitZumSenden = true;
}
function setCatalog(msg) {
	var rows = document.getElementById("cat").rows.length;
	for (var i = 0; i < rows; i++) {
		var row = document.getElementById("cata" + i);
		if (row.innerText == msg.catalog) {
			row.style.backgroundColor = 'red';
		} else {
			row.style.backgroundColor = '#e6ee9c';
		}
	}
}
function showPlayerList(msg) {
	var playerTable = document.getElementById("play");
	var player = '<tr><td>Player</td><td>Score</td></tr>';
	for (i = 0; i < msg.usernames.length; i++) {
		player = player + '<tr><td>' + msg.usernames[i].username + '</td><td>'
				+ msg.scores[i].score + '</td></tr>';
	}
	playerTable.innerHTML = player;
}
function showPlayerListSSE(event) {
	console.log(event);
	var data=JSON.parse(event.data);
	console.log(data);
	var playerTable = document.getElementById("play");
	var player = '<tr><td>Player</td><td>Score</td></tr>';
	for (i = 0; i < data.usernames.length; i++) {
		player = player + '<tr><td>' + data.usernames[i].username + '</td><td>'
				+ data.scores[i].score+ '</td></tr>';
	}
	playerTable.innerHTML = player;
}
function changeColor(event) {
	var element = event.currentTarget
	var JSON = "{\"type\":\"CCH\", \"catalog\":\"" + element.innerText + "\"}";
	socket.send(JSON);
}
function deactivateLogin(msg) {
	var elem = document.getElementById("loginform");
	elem.parentNode.removeChild(elem);
	if (msg.status == "true") {
		var elem = document.getElementById("main");
		elem.innerHTML = "";
		elem.innerHTML = '<table id="loginAnzeige" class="tableMain" align="center"><tr><td><input id="startButton" type="submit" value="starten" /></td></tr></table>';
		var startButton = document.getElementById("startButton");
		startButton.addEventListener("click", startGame, false);
	} else {
		var elem = document.getElementById("main");
		elem.innerHTML = "";
		elem.innerHTML = '<p>Warten Sie bis der Spielleiter das Spiel beginnt</p>';
	}

}
function startGame() {
	var JSON = "{\"type\":\"STG\"}";
	socket.send(JSON);
}
function errorHandle(msg) {
	console.log("ERR"+msg.fehler);
	alert(msg.fehler);
}
function addStartButton(msg) {
	var elem = document.getElementById("main");
	elem.innerHTML = "";
	elem.innerHTML = '<table id="loginAnzeige" class="tableMain" align="center"><tr><td><input id="startButton" type="submit" value="starten" /></td></tr></table>';
	var startButton = document.getElementById("startbutton");
	startButton.addEventListener("click", startGame, false);
}
function questionRequest() {
	var JSON = "{\"type\":\"QRQ\"}";
	socket.send(JSON);
}
function showQuestion(msg) {
	var elem = document.getElementById("main");
	var question = "";
	question += '<table id="fragen" class="tableFragen" align="center"><tr><td align="center" width="500px">'
			+ msg.frage + '</td></tr>';
	for (i = 0; i < msg.antworten.length; i++) {
		question += '<tr><td align="center" colspan="2" width="500px">'
				+ msg.antworten[i].antwort + '</td></tr>';
	}
	question += '</table>';
	question += '</table>';
	elem.innerHTML = "";
	elem.innerHTML = question;
	var frag = document.getElementById("fragen");
	for (i = 1; i < frag.rows.length; i++) {

		frag.rows[i].addEventListener("click", auswerten, false);
	}
}
function waitPlayer(msg) {
	var elem = document.getElementById("main");
	if(msg.status==0){
		elem.innerHTML = '<table id="endAnzeige" class="tableMain" align="center"><tr><td><b>Warten Sie bis alle Spieler fertig sind!</b></td></tr></table>';
	}
	if(msg.status==1){
		elem.innerHTML = '<table id="endAnzeige" class="tableMain" align="center"><tr><td><b>Das Spiel ist beendet</b></td></tr></table><p><input type="button" value="Neues Spiel" id="refreshbutton" onClick="window.location.reload()">';
		//var button=document.getElementById("refresh");
		//button.addEventListener("click",neustart,false);
	}
}
function auswerten(event) {
	var element = event.currentTarget;
	var index = element.rowIndex - 1;
	var JSON = "{\"type\":\"QAS\", \"index\":\"" + index + "\"}";
	socket.send(JSON);
}
function questionResult(msg) {
	var antwort = document.getElementById("fragen");
	var correct = msg.correct;
	correct = correct + 1;
	console.log(correct);
	var cor = parseInt(msg.correct);
	cor = cor + 1;
	console.log(cor);
	if (msg.mine != cor) {
		antwort.rows[msg.mine].style.background = "red";
	}

	antwort.rows[cor].style.background = "#00CC00";

	window.setTimeout("questionRequest()", 2000);
}s
