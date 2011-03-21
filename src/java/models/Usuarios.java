package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import utils.AbstractModel;

@Entity
public class Usuarios extends AbstractModel {

	private Integer idUsuario;
	private String nome;
	private String login;
	private String senha;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	@Transient
	@Override
	public Integer getIdTable() {
		return 2;
	}

	@Transient
	@Override
	public Integer getId() {
		return getIdUsuario();
	}

}
