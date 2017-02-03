package serv;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import de.fhwgt.quiz.application.Quiz;

public class Session implements HttpSessionListener {

	public Session() {
	}

	public void sessionCreated(HttpSessionEvent session) {
		HttpSession Session = session.getSession();
		System.out.println("SessionListener: Session erzeugt");

		// Session nach 1 Minuten zerstören
		Session.setMaxInactiveInterval(1000);
		System.out.println("SessionListener:  Sessiondauer= " + Session.getMaxInactiveInterval());

		Quiz quiz = Quiz.getInstance();
		if (quiz != null) {
			System.out.println("Set attribute");
			Session.setAttribute("quiz", quiz);
		}

	}

	public void sessionDestroyed(HttpSessionEvent session) {

		HttpSession Session = session.getSession();

		String id = Session.getId();

		System.out.println("SessionListener: Session " + id + " deletet");
	}
}