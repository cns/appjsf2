package controllers;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import models.Categorias;
import utils.AbstractBean;

@ManagedBean
@ViewScoped
public class CategoriasBean extends AbstractBean<Categorias> {

	public CategoriasBean() {
		super(Categorias.class);
		setPanelHeader("Controle de Categorias");
	}
	
}