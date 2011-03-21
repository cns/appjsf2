package utils;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthPhaseListener implements PhaseListener {

	private Credentials credentials = new Credentials();
	private HttpSession session;

	public AuthPhaseListener() {
		session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		
	}

	public void afterPhase(PhaseEvent pe) {
		if (pe.getPhaseId() == PhaseId.RESTORE_VIEW || pe.getPhaseId() == PhaseId.INVOKE_APPLICATION) {
			credentials = getCredentials();
			if (!credentials.getLogged() && !pe.getFacesContext().getViewRoot().getViewId().equals("/login.xhtml")) {
				String viewId = pe.getFacesContext().getViewRoot().getViewId();
				HttpServletResponse response = (HttpServletResponse) pe.getFacesContext().getExternalContext().getResponse();
				HttpServletRequest request = (HttpServletRequest) pe.getFacesContext().getExternalContext().getRequest();
				String path = request.getContextPath();
				try {
					
					credentials.setViewToRedirect(viewId);
					session.setAttribute("credentials", credentials);
					response.sendRedirect(path+"/login.xhtml");
					FacesContext.getCurrentInstance().responseComplete();
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You need to be logged to access this page", "You need to be logged to access this page"));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Credentials getCredentials() {
		session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		Credentials temp = (Credentials) session.getAttribute("credentials");
		if (temp == null)
			session.setAttribute("credentials", credentials);
		else
			credentials = temp;
		return credentials;
	}

	public void beforePhase(PhaseEvent pe) {
		if (pe.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			
		}
	}

	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

}
