package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import utils.AbstractModel;

@Entity
public class Categorias extends AbstractModel {

	private Integer idCategoria;
	private String nome;
	private String descricao;

	@Column
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Id
	@Column
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(Integer idCategoria) {
		this.idCategoria = idCategoria;
	}

	@Column
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	@Transient
	public Integer getIdTable() {
		return 1;
	}

	@Override
	@Transient
	public Integer getId() {
		return getIdCategoria();
	}

}
