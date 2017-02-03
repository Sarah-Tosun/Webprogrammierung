package main;

import java.io.IOException;
import java.util.TimerTask;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import de.fhwgt.quiz.application.Player;
import de.fhwgt.quiz.application.Question;
import de.fhwgt.quiz.application.Quiz;
import de.fhwgt.quiz.error.QuizError;
import de.fhwgt.quiz.loader.LoaderException;

@ServerEndpoint("/Login")
public class Login {
	Quiz quiz = Quiz.getInstance();
	Player player = null;
	boolean gameFinished = false;
	QuizError quizerror;

	@OnError
	public void error(Session session, Throwable t) {
		System.out.println("Fehler beim Öffnen des Sockets");
	}

	@OnOpen
	// Ein Client meldet sich an und er�ffnet eine neue Web-Socket-Verbindung
	public void open(Session session, EndpointConfig conf) {
		ConnectionManager.addSession(session);
		System.out.println("Open Socket mit Session-ID= " + session.getId());
		if (session.isOpen()) {
			if (quiz.getPlayerList().size() != 0) {
				if (quiz.getCurrentCatalog() != null) {
					String senden = "{\"type\":\"CCH\", \"catalog\":\"" + quiz.getCurrentCatalog().getName() + "\"}";
					send(senden, session);
				}
			}
		}
		sendPlayerList();

	}

	@OnClose
	// Client meldet sich wieder ab
	public void close(Session session, CloseReason reason) {
		String id = session.getId();
		ConnectionManager.SessionRemove(session);
		System.out.println("Close Client.");
		if (player != null) {
			quizerror = new QuizError();
			quiz.removePlayer(player, quizerror);
			if (quizerror.isSet()) {
				if(!gameFinished){
					String senden = "{\"type\":\"ERR\", \"fehler\": \"" + quizerror.getDescription() + "\"}";
					broadcast(senden);
				}
				
			}

		}

		System.out.println("Anzahl Spieler nach Entfernen " + quiz.getPlayerList().size());
	}

	@OnMessage
	public void echoTextMessage(Session session, String msg, boolean last) throws LoaderException {

		JSONObject jobject = new JSONObject();
		JSONParser jparser = new JSONParser();
		String type = null;
		String response = null;
		String catalog = null;
		try {
			jobject = (JSONObject) jparser.parse(msg);
			type = (String) jobject.get("type");
		} catch (Exception e) {
			System.out.println("Konnte nicht parsen");
		}
		System.out.println("Server Empfange Message vom Typ: " + type);
		switch (type) {
		case "LRQ": // Login Request
			quizerror = new QuizError();
			player = quiz.createPlayer((String) jobject.get("name"), quizerror);
			if (quizerror.isSet()) {
				response = "{\"type\":\"ERR\",\"fehler\":\"" + quizerror.getDescription() + "\"}";
				send(response, session);
			} else {
				if (player.isSuperuser()) {
					response = "{\"type\":\"LOK\",\"status\":\"" + player.isSuperuser() + "\"}";
				} else {
					response = "{\"type\":\"LOK\",\"status\":\"" + player.isSuperuser() + "\"}";
				}
				send(response, session);
				sendPlayerList();
			}
			break;
		case "CCH": // Catalog Request
			quizerror = new QuizError();
			if (player != null && player.isSuperuser()) {
				catalog = (String) jobject.get("catalog");
				response = "{\"type\":\"CCH\",\"catalog\":\"" + catalog + "\"}";
				quiz.changeCatalog(player, catalog, quizerror);
				if (quizerror.isSet()) {
					response = "{\"type\":\"ERR\",\"fehler\":\"" + quizerror.getDescription() + "\"}";
					send(response, session);
				} else {
					broadcast(response);
				}

			}
			break;
		case "STG": // Start Game
			if (quiz.getCurrentCatalog() != null) {
				if (quiz.getPlayerList().size() >= 2) {
					quiz.getCatalogByName(quiz.getCurrentCatalog().getName());
					quizerror = new QuizError();
					quiz.startGame(player, quizerror);
					if (!quizerror.isSet()) {
						response = "{\"type\":\"STG\"}";
						broadcast(response);
					} else {
						response = "{\"type\":\"ERR\", \"fehler\": \"" + quizerror.getDescription() + "\"}";
						send(response, session);
					}
				} else {
					response = "{\"type\":\"ERR\", \"fehler\": \"Nicht ausreichend Spieler!\"}";
					send(response, session);
				}
			} else {
				response = "{\"type\":\"ERR\", \"fehler\": \"Suchen Sie ein Katalog aus!\"}";
				send(response, session);
			}
			break;

		case "QRQ": // Frage Anfordern

			TimerTask timert = new TimerTask() {
				@Override
				public void run() {
					System.out.println("im TimerTask");
					quizerror = new QuizError();
					quiz.answerQuestion(player, 1, quizerror);
					if (quizerror.isSet()) {
						String response = "{\"type\":\"ERR\", \"fehler\": \"" + quizerror.getDescription() + "\"}";
						send(response, session);
					} else {
						String senden = "{\"type\":\"QUR\",\"correct\":\"" + 2 + "\",\"mine\":\"" + 3 + "\"}";
						System.out.println(" QRE an js senden: " + senden);
						send(senden, session);
					}

				}
			};
			quizerror = new QuizError();
			Question frage = quiz.requestQuestion(player, timert, quizerror);
			if (quizerror.isSet()) {
				response = "{\"type\":\"ERR\", \"fehler\": \"" + quizerror.getDescription() + "\"}";
				send(response, session);
			} else {
				if (frage != null) {
					int i = 0;
					response = "{\"type\":\"QRE\", \"frage\":\"" + frage.getQuestion() + "\", \"antworten\": [";
					for (i = 0; i < frage.getAnswerList().size() - 1; i++) {
						response = response + "{\"antwort\":\"" + frage.getAnswerList().get(i) + "\"},";
					}
					response = response + "{\"antwort\":\"" + frage.getAnswerList().get(i) + "\"}]}";

					send(response, session);
				} else {
					quiz.setDone(player);
					int finish = 0;
					for (int i = 0; i < quiz.getPlayerList().size(); i++) {
						Player p = (Player) quiz.getPlayerList().toArray()[i];
						if (p.isDone() == false) {
							finish = 1;
							response = "{\"type\":\"WOP\",\"status\":\"0\"}";
							send(response, session);
							break;
						}

					}
					if (finish == 0) {
						gameFinished = true;
						response = "{\"type\":\"WOP\",\"status\":\"1\"}";
						broadcast(response);
					}
					
				}
			}

			break;
		case "QAS": // Frage Antwort
			String index = (String) jobject.get("index");
			quizerror = new QuizError();
			Long correct = quiz.answerQuestion(player, Long.parseLong(index),quizerror);
			if(quizerror.isSet()){
				response = "{\"type\":\"ERR\", \"fehler\": \"" + quizerror.getDescription() + "\"}";
				send(response, session);
			}
			else{
				int in = (Integer.parseInt(index) + 1);
				System.out.println("Score Player:" + player.getName() + "Score: " + player.getScore());
				if (correct != -1) {
					response = "{\"type\":\"QUR\",\"correct\":\"" + correct + "\",\"mine\":\"" + in + "\"}";
					send(response, session);
					Player[] array = new Player[6];
					for (int j = 0; j < quiz.getPlayerList().size(); j++) {
						array[j] = (Player) quiz.getPlayerList().toArray()[j];
					}
					Player temp;
					for (int j = quiz.getPlayerList().size(); j > 1; j--) {
						for (int i = 1; i < j; i++) {
							if (array[i - 1].getScore() > array[i].getScore()) {
								temp = array[i - 1];
								array[i - 1] = array[i];
								array[i] = temp;

							}
						}
					}
					sendPlayerList();
				}
			}
			
		}
	}

	public void broadcast(String msg) {
		// Broadcasting : JSON-String an alle Web-Socket-Verbindungen senden
		System.out.println(msg);
		for (int i = 0; i < ConnectionManager.SessionCount(); i++) {
			Session s = ConnectionManager.getSession(i);
			try {
				s.getBasicRemote().sendText(msg, true);
			} catch (IOException e) {
				// ignore
			}
		}
	}

	public void send(String msg, Session s) {
		try {
			System.out.println(msg);
			s.getBasicRemote().sendText(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendPlayerList() {
		// if (quiz.getPlayerList().size() > 0) {
		// String response = "{\"type\":\"LST\", \"usernames\": [";
		// for (int i = 0; i < quiz.getPlayerList().size() - 1; i++) {
		// Player p = (Player) quiz.getPlayerList().toArray()[i];
		// response = response + "{\"username\":\"" + p.getName() + "\"},";
		// }
		// Player p = (Player)
		// quiz.getPlayerList().toArray()[quiz.getPlayerList().size() - 1];
		// response = response + "{\"username\":\"" + p.getName() + "\"}]";
		// response = response + ", \"scores\": [";
		// for (int i = 0; i < quiz.getPlayerList().size() - 1; i++) {
		// Player s = (Player) quiz.getPlayerList().toArray()[i];
		// response = response + "{\"score\":\"" + s.getScore() + "\"},";
		// }
		// Player s = (Player)
		// quiz.getPlayerList().toArray()[quiz.getPlayerList().size() - 1];
		// response = response + "{\"score\":\"" + s.getScore() + "\"}]}";
		// broadcast(response);
		// }
	}

}