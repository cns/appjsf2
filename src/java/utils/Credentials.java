package utils;

import java.io.Serializable;

public class Credentials implements Serializable {

	private Integer id;
	private String username;
	private String password;
	private String viewToRedirect;
	private Boolean logged;
	private AbstractModel user;

	public Credentials() {
		logged = false;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Boolean getLogged() {
		return logged;
	}

	public void setLogged(Boolean logged) {
		this.logged = logged;
	}

	public AbstractModel getUser() {
		return user;
	}

	public void setUser(AbstractModel user) {
		this.user = user;
	}

	public String getViewToRedirect() {
		return viewToRedirect;
	}

	public void setViewToRedirect(String viewToRedirect) {
		if (!viewToRedirect.equals("/index.xhtml"))
			this.viewToRedirect = viewToRedirect;
	}

}
