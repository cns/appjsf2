package utils;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@ManagedBean
@SessionScoped
public class Authenticator implements Serializable {

	private Credentials credentials = new Credentials();
	private String currentPage = "index";
	private String previousPage;
	private HttpSession session;
	private HttpServletResponse response;
	private HttpServletRequest request;
	private String path;

	private boolean showDialog = false;
	private Boolean changePassword = false;
	private String message = "Teste de mensagem";

	public Authenticator() {}

	public void authenticate() {
		Credentials olderCredentials = getCredentials();
		if (credentials.getUsername().equals("admin") && credentials.getPassword().equals("admin")) {
			credentials.setLogged(true);
			response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			path = request.getContextPath();
			getSession().setAttribute("credentials", credentials);
			try {
					if (olderCredentials.getViewToRedirect() != null)
						response.sendRedirect(path+olderCredentials.getViewToRedirect());
					else
						response.sendRedirect(path);
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Hello", "Hello"));
					FacesContext.getCurrentInstance().responseComplete();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
		}
		else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Autenticação falhou", "Autenticação falhou"));
		}
	}

	public void logout() {
		try {
			credentials = new Credentials();
			getSession().setAttribute("credentials", credentials);
			setCurrentPage("index");
			FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/login.xhtml");
		} catch (Exception ex) {
			ex.printStackTrace();
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

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public HttpSession getSession() {
		if (session != null)
			return session;
		else
			return (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isShowDialog() {
		return showDialog;
	}

	public void setShowDialog(boolean showDialog) {
		this.showDialog = showDialog;
	}

	public String getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(String currentPage) {
		if (!this.currentPage.equals(currentPage) && !changePassword) {
			previousPage = this.currentPage;
			this.currentPage = currentPage;
		}
	}

	public void previousPage() {
		currentPage = previousPage;
	}
}
