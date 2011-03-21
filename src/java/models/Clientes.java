package models;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.eayrad.api.beanvalidation.annotations.Cpf;
import utils.AbstractModel;

@Entity
public class Clientes extends AbstractModel {

	private Integer idCliente;
	private Categorias idCategoria;
	private String nome;
	@Cpf
	private String cpf;
	private Date dataCadastro = new Date();
	private String comentarios;
	private Date aniversario;
	private String tipo;
	private Integer numeroAcessos = 0;
	private Boolean ativo;

	@Column
	@Temporal(TemporalType.DATE)
	public Date getAniversario() {
		return aniversario;
	}

	public void setAniversario(Date aniversario) {
		this.aniversario = aniversario;
	}

	@Column(nullable=false)
	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	@Column(columnDefinition="TEXT")
	public String getComentarios() {
		return comentarios;
	}

	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}

	@Column(length=14)
	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	@Column(nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idCategoria")
	public Categorias getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(Categorias idCategoria) {
		this.idCategoria = idCategoria;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}

	@Column(length=50, nullable=false)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(nullable=false)
	public Integer getNumeroAcessos() {
		return numeroAcessos;
	}

	public void setNumeroAcessos(Integer numeroAcessos) {
		this.numeroAcessos = numeroAcessos;
	}

	@Column(nullable=false)
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@Transient
	@Override
	public Integer getIdTable() {
		return 3;
	}

	@Transient
	@Override
	public Integer getId() {
		return getIdCliente();
	}

}
