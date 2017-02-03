<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="UTF-8">
<title>Multiplayer-Quiz</title>
<link rel="stylesheet" type="text/css" href="quizcss.css">
<script src=aufgabe3.js>
	
</script>
<%@ page import="java.util.*, java.text.*"%>
<%@ page import="de.fhwgt.quiz.application.Game"%>
<%@ page import="de.fhwgt.quiz.application.Quiz"%>
<%@ page import="de.fhwgt.quiz.application.Player"%>
<%@ page import="de.fhwgt.quiz.error.QuizError"%>
</head>
<body>
	<%!private static int zaehler = 0;
	private static String catalog[] = new String[10];
	private static int i = 0;
	private int anzahlSpieler = 0;
	private boolean read = false;%>
	<%
		System.out.println("Start der jsp-Seite zaehler= " + zaehler);
		if (session == null) {
			System.out.println("jsp-Seite: Session oder zaehler is null. Servlet  aufrufen");
			request.getRequestDispatcher(response.encodeURL("Servlet")).include(request, response);
		}
		zaehler++;
	%>
	<%
		Quiz quiz = (Quiz) session.getAttribute("quiz");
	%>
	<%
		anzahlSpieler = quiz.getPlayerList().size();
	%>
	<%=quiz.getPlayerList().size()%>
	<%
		if (!read) {
			for (String key : quiz.getCatalogList().keySet()) {
	%>
	<%
		catalog[i] = key.toString();
	%>
	<%
		i++;
	%>
	<%
		}
			read = true;
		}
	%>
	<div class="container">
		<header>
		<h2 name="laufschrift">
			<img src="idee.png" alt="Quiz" id="icon"> Webquiz
		</h2>
		</header>
		<section>
		<form id="loginform" action="Serv" method="GET">
			<p>
				Username: <br> <input name="username" type="text" id="username"
					value="" size="30" maxlength="25">
			</p>
			<input type="submit" value="Login" id="loginbutton"> <input
				type="button" value="Start" id="startbutton">
		</form>
		<div id="fragenAnzeige">
			<table class="tableFragen">
				<tr>
					<td>Welcher Mechanismus kann unter Unix zur Kommunikation über
						das Netzwerk verwendet werden?</td>
				</tr>
			</table>
			<table id="antwort1" class="tableAntworten">
				<tr>
					<td>1. Sockets</td>
				</tr>
			</table>
			<table id="antwort2" class="tableAntworten">
				<tr>
					<td>2. Pipes</td>
				</tr>
			</table>
			<table id="antwort3" class="tableAntworten">
				<tr>
					<td>3. Message Queues</td>
				</tr>
			</table>
			<table id="antwort4" class="tableAntworten">
				<tr>
					<td>4. Semaphore</td>
				</tr>
			</table>
		</div>
		</section>
		<aside class="eins">
		<p>
			<img src="cata.png" alt="Catalogs" id="icon"> Catalogs
		</p>
		<table id="cat">
			<tr>
				<td id="cata1"><%=catalog[0]%></td>
			</tr>
			<tr>
				<td id="cata2"><%=catalog[1]%></td>
			</tr>
			<tr>
				<td id="cata3"><%=catalog[2]%></td>
			</tr>
		</table>
		</aside>
		<aside class="eins">
		<p>
			<img src="high.png" alt="Highscore" id="icon"> Highscore
		</p>
		<table id="play">
			<tr>
				<td>Player:</td>
				<td>Score:</td>
			</tr>

			<tr id="player1">
				<td>
					<%
						if (anzahlSpieler > 0) {
					%> <%
 	Player p = (Player) quiz.getPlayerList().toArray()[0];
 %><%=p.getName()%>
					<%
						}
					%>
				</td>
				<td></td>

			</tr>


			<tr id="player2">
				<td>
					<%
						if (anzahlSpieler > 1) {
					%> <%
 	Player p = (Player) quiz.getPlayerList().toArray()[1];
 %><%=p.getName()%>
					<%
						}
					%>
				</td>
				<td></td>
			</tr>


			<tr id="player3">
				<td>
					<%
						if (anzahlSpieler > 2) {
					%> <%
 	Player p = (Player) quiz.getPlayerList().toArray()[2];
 %><%=p.getName()%>
					<%
						}
					%>
				</td>
				<td>
				<td>
			</tr>


			<tr id="player4">
				<td>
					<%
						if (anzahlSpieler > 3) {
					%> <%
 	Player p = (Player) quiz.getPlayerList().toArray()[3];
 %><%=p.getName()%>
					<%
						}
					%>
				</td>
				<td>
				<td>
			</tr>

		</table>
		</aside>
		<footer>
		<p id="unten">© Copyright, Gruppe 4</p>
		</footer>
	</div>
</body>
</html>
