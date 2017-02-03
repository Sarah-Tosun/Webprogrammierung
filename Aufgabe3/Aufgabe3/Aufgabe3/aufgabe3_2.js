

console.log("HI");

window.addEventListener("load",init,true);
	
var text;
function init(){
	var header=document.getElementsByName("laufschrift");
	text = header[0].childNodes[1].nodeValue;
	setInterval(laufschrift,300);
	//login
	document.getElementById("loginbutton").addEventListener("click",login);
	console.log(document.getElementById("loginbutton").addEventListener("click",login));
	//catalog
	document.getElementById("cata1").addEventListener("click",catalog,false);
	document.getElementById("cata2").addEventListener("click",catalog,false);
	document.getElementById("cata3").addEventListener("click",catalog,false);
}

function laufschrift(){
	text=text.slice(1,text.length)+text.slice(0,1);
	document.getElementsByName("laufschrift")[0].childNodes[1].nodeValue=text;
	
}

function login(){
	console.log("HI2")
	var username = document.getElementById("username").value;
	console.log(username);
	if(username == 0){
		alert("Bitte Username eingeben");
	}
	else{
		var table = document.getElementById("play");
		//console.log(table.innerHTML);
		if(table.rows.length <= 4){
			var row=table.insertRow(1);
			row.addEventListener("click",movePlayer,false);
			var playercell=row.insertCell(0);
			var scorecell=row.insertCell(1);
			playercell.innerHTML = username;
			scorecell.innerHTML = "0";
		}
		else{
			alert("Maximale Spieleranzahl erreicht");
		}
		console.log(document.getElementById("loginform").style.visibility=="hidden");
        
        if((table.rows.length >= 3) && (document.getElementById("loginform").style.visibility==false)){
			console.log("HI3");
			var button=document.getElementById("startbutton");
            button.style.visibility="visible";
            button.addEventListener("click",start,false);
		}

	}
}

function catalog(event){
	document.getElementById("cata1").style.backgroundColor= "#e6ee9c";
	document.getElementById("cata2").style.backgroundColor = "#e6ee9c";
	document.getElementById("cata3").style.backgroundColor = "#e6ee9c";
	var selectedCata= event.currentTarget;
	selectedCata.style.backgroundColor = "red";
	
}
function movePlayer(event){
	var currentPlayer = event.currentTarget;
	currentPlayer.removeEventListener("click",movePlayer);
	console.log(currentPlayer);
	var table= currentPlayer.parentNode.parentNode;
	console.log(currentPlayer.parentNode.parentNode);
	var row=table.insertRow(1);    
	row.addEventListener("click",movePlayer);
	var playercell = row.insertCell(0);
	var scorecell = row.insertCell(1);
	playercell.innerHTML = currentPlayer.childNodes[0].innerHTML;
	scorecell.innerHTML = currentPlayer.childNodes[1].innerHTML;
	//Remove
	for(i=2;i<table.rows.length;i++){
		if(currentPlayer.childNodes[0].innerHTML==table.rows[i].cells[0].innerHTML){
			table.deleteRow(i);
		}
	}
}
function start(){
    document.getElementById("startbutton").style.visibility="hidden";
    document.getElementById("loginform").style.visibility="hidden";
    document.getElementById("fragenAnzeige").style.visibility="visible";
}


