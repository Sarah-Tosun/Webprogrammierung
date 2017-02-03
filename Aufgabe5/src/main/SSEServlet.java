package main;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.fhwgt.quiz.application.Player;
import de.fhwgt.quiz.application.Quiz;

/**
 * Servlet implementation class SSEServlet
 */
@WebServlet("/SSEServlet")
public class SSEServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public SSEServlet() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Quiz quiz = Quiz.getInstance();

		// cache abstellen
		response.setHeader("pragma", "no-cache,no-store");
		response.setHeader("cache-control", "no-cache,no-store,max-age=0,max-stale=0");

		// Protokoll auf Server Sent Events einstellen
		response.setContentType("text/event-stream");
		response.setCharacterEncoding("UTF-8");
		PrintWriter pw = response.getWriter();
		
		while(!pw.checkError()){	
			if (quiz.getPlayerList().size() > 0) {
				Player[] array = new Player[6];
				for (int j = 0; j < quiz.getPlayerList().size(); j++) {
					array[j] = (Player) quiz.getPlayerList().toArray()[j];
				}
				Player temp;
				for (int j = quiz.getPlayerList().size(); j > 1; j--) {
					for (int i = 1; i < j; i++) {
						if (array[i - 1].getScore() < array[i].getScore()) {
							temp = array[i - 1];
							array[i - 1] = array[i];
							array[i] = temp;

						}
					}
				}
				pw.print("event: LST\n");
				String message = "data:{\"usernames\": [";
				int j;
				for (j = 0; j < quiz.getPlayerList().size() - 1; j++) {;
					message = message + "{\"username\":\"" + array[j].getName() + "\"},";
				}
				message = message + "{\"username\":\"" + array[j].getName() + "\"}]";
				message = message + ", \"scores\": [";
				int v;
				for (v = 0; v < quiz.getPlayerList().size()-1; v++) {
					message = message + "{\"score\":\"" + array[v].getScore() + "\"},";
				}
				message = message + "{\"score\":\"" + array[v].getScore() + "\"}]}\n\n";
				pw.print(message);
				pw.checkError();
				//pw.flush();
				quiz.waitPlayerChange();
			}
		}
		pw.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
