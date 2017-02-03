package serv;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.fhwgt.quiz.application.Player;
import de.fhwgt.quiz.application.Quiz;
import de.fhwgt.quiz.error.QuizError;
import de.fhwgt.quiz.loader.FilesystemLoaderXML;
import de.fhwgt.quiz.loader.LoaderException;

/**
 * Servlet implementation class test
 */
@WebServlet("/Serv")
public class Serv extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Serv() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Session erzeugen oder benutzen
		HttpSession session = request.getSession(true);
		session.setMaxInactiveInterval(1000);
		String name = request.getParameter("username");
		System.out.print(name);
		Quiz quiz = (Quiz) session.getAttribute("quiz");
		if (name.length() > 0) {
			quiz.createPlayer(name, new QuizError());
		}
		FilesystemLoaderXML fisXML = new FilesystemLoaderXML("/Fragenkatalog/");
		System.out.println("file:/" + System.getProperty("user.dir") + "/Fragenkatalog/");
		quiz.initCatalogLoader(fisXML);

		System.out.println("--------------------------anzahl kataloge Anfang------------------------");
		try {
			System.out.println(fisXML.getCatalogs().size());
			System.out.println(quiz.getCatalogList().size());

		} catch (LoaderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Folgende Kataloge wurden gelesen:");

		try {
			if (fisXML.getCatalogs() == null) {
				System.out.println("nichts gefunden");
				return;
			}
			for (String key : fisXML.getCatalogs().keySet()) {
				System.out.println(key);
			}
		} catch (LoaderException e) {
			e.printStackTrace();
		}

		System.out.println("");
		System.out.println("");
		System.out.println("--------------------------anzahl kataloge ende------------------------");

		RequestDispatcher dispatcher = request.getRequestDispatcher("quiz.jsp");
		dispatcher.forward(request, response);

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Servlet</title>");
		out.println("</head>");
		out.println("<body>");
		// String name = request.getParameter("name");
		// out.println("name: "+name);
		Player p = (Player) quiz.getPlayerList().toArray()[0];
		out.println("Spielername :  " + p.getName());
		out.println("anzahl der spieler:" + quiz.getPlayerList().size());
		out.println("name in der url:" + name);
		out.println("</body>");
		out.println("</html>");

		out.close();

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
