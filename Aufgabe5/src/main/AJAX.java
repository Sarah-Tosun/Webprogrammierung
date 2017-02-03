package main;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import de.fhwgt.quiz.application.Quiz;
import de.fhwgt.quiz.loader.FilesystemLoaderXML;
import de.fhwgt.quiz.loader.LoaderException;

/**
 * Servlet implementation class AJAX
 */
@WebServlet("/AJAX")
public class AJAX extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static String catalog[] = new String[10];
	public static int i = 0;

	/**
	 * Default constructor.
	 */
	public AJAX() {
		// TODO Auto-generated constructor stub
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Quiz quiz = Quiz.getInstance();
		FilesystemLoaderXML fisXML = new FilesystemLoaderXML("/Fragenkatalog/");
		quiz.initCatalogLoader(fisXML);
		System.out.println("Kataloge gelesen");

		response.setContentType("text/xml");
		PrintWriter writer = response.getWriter();
		try {
			writer.print("<catalogList>");
			for (String key : quiz.getCatalogList().keySet()) {
				catalog[i] = key.toString();
				writer.print("<catalog>" + catalog[i] + "</catalog>");
				i++;
			}
			writer.print("</catalogList>");
			i = 0;
		} catch (LoaderException e) {

		}
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
