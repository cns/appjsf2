package controllers;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import models.Categorias;
import models.Clientes;
import utils.AbstractBean;

@ManagedBean
@ViewScoped
public class ClientesBean extends AbstractBean<Clientes> {

	public ClientesBean() {
		super(Clientes.class);
		setPanelHeader("Controle de Categorias");
	}

	public Integer getIdCategoria() {
		if (getObject().getIdCategoria() != null)
			return getObject().getIdCategoria().getIdCategoria();
		else
			return null;
	}

	public void setIdCategoria(Integer value) {
		if (value != null) {
			Categorias categorias = (Categorias) getSession().load(Categorias.class, value);
			getObject().setIdCategoria(categorias);
		}
		else {
			getObject().setIdCategoria(null);
		}
	}

}
