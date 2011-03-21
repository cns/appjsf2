package utils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import org.eayrad.api.jsf.utils.Export;
import org.eayrad.api.jsf.utils.ExportableColumn;
import org.eayrad.api.persistence.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.primefaces.model.LazyDataModel;

public abstract class AbstractBean<M extends AbstractModel> implements Serializable {

	private Session session;
	private M object;
	private Class objectClass;
	private List<M> objects;
	private Transaction tx;
	private DataModel<M> dataModel;
	private LazyDataModel<M> lazyDataModel;
	private Object[] exportableColumns;
	private String search = "";
	private String panelHeader = "Controle de Objetos";
	private String panelFooter = "<table style=\"width: 100%; margin: 0; padding: 0;\"><tr><td align=\"left\" style=\"width: 50%\">Teste</td><td align=\"right\">Sistema Criado com o EasyRAD</td></tr></table>";
	private Integer page = 1;
	private Integer rows = 5;
	private Integer objectsSize;
	private Integer INDEXPANEL = 1;
	private Integer INSERTPANEL = 2;
	private Integer DETAILPANEL = 3;
	private Integer EXPORTPANEL = 4;
	private Integer currentPanel = INDEXPANEL;
	private Integer previousPanel = INDEXPANEL;
	private Boolean lazy = true;

	public AbstractBean(Class objClass) {
		objectClass = objClass;
		try {
			create();
			session = HibernateUtil.getSession();
		}
		catch (Exception e) {
			sendException(e);
		}
	}

	/*
	 * database operation
	 */
	public void startTransaction() {
		try {
			tx = session.beginTransaction();
		}
		catch (Exception e) {
			sendException(e);
		}
	}

	public boolean commit() {
		try {
			tx.commit();
			return true;
		}
		catch (Exception e) {
			sendException(e);
			tx.rollback();
			return false;
		}
	}

	public void create() {
		try {
			object = (M) objectClass.newInstance();
		}
		catch (Exception e) {
			sendException(e);
		}
	}

	public void save() {
		startTransaction();
		session.save(getObject());
		if (commit()) {
			addSuccessMessage(getMessage("saveSuccess"));
			previousPanel();
			list();
		}
		else {
			addErrorMessage(getMessage("saveError"));
		}
	}

	public void delete() {
		refresh();
		startTransaction();
		session.delete(getObject());
		list();
		if (commit()) {
			create();
			setCurrentPanel(INDEXPANEL);
			addSuccessMessage(getMessage("deleteSuccess"));
		}
		else {
			addErrorMessage(getMessage("deleteError"));
		}
	}

	public void refresh() {
		try {
			if (dataModel != null && dataModel.isRowAvailable()) {
				setObject(dataModel.getRowData());
			}
			session.refresh(getObject());
		}
		catch (Exception e) {
			sendException(e);
		}
	}

	public void cancel() {
		try {
			setObject((M) objectClass.newInstance());
		}
		catch (Exception e) {
			sendException(e);
		}
		previousPanel();
	}

	public void insert() {
		try {
			setObject((M) objectClass.newInstance());
			setCurrentPanel(INSERTPANEL);
		}
		catch (Exception e) {
			sendException(e);
		}
	}

	public void edit() {
		refresh();
		setCurrentPanel(INSERTPANEL);
	}

	public void detail() {
		refresh();
		setCurrentPanel(DETAILPANEL);
	}

	public void exportTable() {
		setCurrentPanel(EXPORTPANEL);
	}

	public void findResults() {
		if (search.length() == 0 || search.equals("*")) {
			objects = null;
			list();
		}
		else {
			Object value = null;
			Class type = null;
			String name = null;
			Disjunction disjunction = Restrictions.disjunction();
			for (Object objs : getSearchFields()) {
				Object[] obj = (Object[]) objs;
				name = obj[0].toString();
				type = (Class) obj[1];
				try {
					value = null;
					if (type == Boolean.class) {
						value = Boolean.parseBoolean(search);
						disjunction.add(Restrictions.eq(name, value));
					}
					else if (type == Integer.class) {
						value = Integer.parseInt(search);
						disjunction.add(Restrictions.eq(name, value));
					}
					else if (type == Double.class) {
						value = Double.parseDouble(search);
						disjunction.add(Restrictions.eq(name, value));
					}
					else if (type == String.class) {
						disjunction.add(Restrictions.ilike(name, search, MatchMode.ANYWHERE));
					}
				} catch (Exception e) { /* this exception is normal because here parsers are tested */ }
			}
			if (lazy)
				lazyDataModel = new AbstractLazyModel(disjunction);
			else
				setObjects(session.createCriteria(objectClass).add(disjunction).list());
			page = 1;
		}
	}

	private ArrayList getSearchFields() {
		Class type = null;
		String name = null;
		ArrayList array = new ArrayList();
		Object[] obj = null;
		for (Method method : objectClass.getDeclaredMethods()) {
			if (method.isAnnotationPresent(Column.class) || method.isAnnotationPresent(Id.class)) {
				obj = new Object[2];
				name = method.getName();
				type = method.getReturnType();
				String m = name.replaceFirst("get", "");
				String s = m.substring(0, 1);
				String r = s.toLowerCase();
				r = r.concat(m.substring(1, m.length()));
				obj[0] = r;
				obj[1] = type;
				array.add(obj);
			}
		}
		return array;
	}

	public void list() {
		setSearch("*");
		objects = null;
		dataModel = null;
		getSession().clear();
	}

	public void export() {
		try {
			String[] headers = {getPanelHeader()};
			Export.exportSheet(getObjects(), getExportParameters(), objectClass.getSimpleName(), headers);
		}
		catch (Exception e) {
			sendException(e);
			addErrorMessage(getMessage("exportError"));
		}
	}

	/*
	 * helpers
	 */
	public void sendException(Exception e) {
		e.printStackTrace();
	}

	public void addMessage(String message) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(message));
	}

	public void addMessage(FacesMessage.Severity sev, String summary) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(sev, summary, summary));
	}

	public void addMessage(FacesMessage.Severity sev, String summary, String message) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(sev, summary, message));
	}

	public void addSuccessMessage(String message) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
	}

	public void addErrorMessage(String message) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
	}

	public void addSuccessMessage(String id, String message) {
		FacesContext.getCurrentInstance().addMessage(id, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
	}

	public void addErrorMessage(String id, String message) {
		FacesContext.getCurrentInstance().addMessage(id, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
	}

	public String getMessage(String id) {
		/**
		 * TODO
		 */
		//ResourceBundle rb = ResourceBundle.getBundle("utils.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
		//return rb.getString(id);
		return id;
	}

	/*
	 * getters and setters
	 */
	public List<M> getObjects() {
		if (objects == null) {
			objects = session.createCriteria(objectClass).list();
			dataModel = new ListDataModel<M>(objects);
			return objects;
		}
		else {
			return objects;
		}
	}

	public void setObjects(List<M> objects) {
		if (objects != null) {
			objectsSize = objects.size();
			dataModel = new ListDataModel<M>(objects);
		}
		this.objects = objects;
	}

	public DataModel<M> getDataModel() {
		if (lazy) {
			if (lazyDataModel == null) {
				lazyDataModel = new AbstractLazyModel();
				return lazyDataModel;
			}
			else {
				return lazyDataModel;
			}
		}
		else {
			if (dataModel == null) {
				objects = session.createCriteria(objectClass).list();
				dataModel = new ListDataModel<M>(objects);
				return dataModel;
			}
			else {
				return dataModel;
			}
		}
	}

	public DataModel<M> getSelectDataModel() {
		objects = session.createCriteria(objectClass).list();
		dataModel = new ListDataModel<M>(objects);
		return dataModel;
	}

	// TODO
	// atualizar para novo objeto exportablecolumn
	public Object[][] getExportParameters() {
		ArrayList<String> gettersList = new ArrayList<String>();
		ArrayList<String> headersList = new ArrayList<String>();
		String m;
		for (Method method : objectClass.getDeclaredMethods()) {
			m = method.getName().replaceFirst("get", "");
			if (method.isAnnotationPresent(Id.class)) {
				if (gettersList.size() > 0) {
					String temp = gettersList.get(0);
					gettersList.add(0, method.getName());
					gettersList.add(temp);
					temp = headersList.get(0);
					headersList.add(0, m);
					headersList.add(temp);
				}
				else {
					gettersList.add(method.getName());
					headersList.add(m);
				}

			}
			else if (method.isAnnotationPresent(Column.class) || method.isAnnotationPresent(JoinColumn.class)) {
				gettersList.add(method.getName());
				headersList.add(m);
			}
		}
		Object[][] par = {gettersList.toArray(), headersList.toArray()};
		return par;
	}

	public ArrayList<ExportableColumn> getAllExportableColumns() {
		ArrayList<ExportableColumn> list = new ArrayList<ExportableColumn>();
		String m;
		for (Method method : objectClass.getDeclaredMethods()) {
			m = method.getName().replaceFirst("get", "");
			if (method.isAnnotationPresent(Id.class)) {
				if (list.size() > 0) {
					ExportableColumn temp = list.get(0);
					list.add(0, new ExportableColumn(m, method.getName()));
					list.add(temp);
				}
				else {
					list.add(new ExportableColumn(m, method.getName()));
				}

			}
			else if (method.isAnnotationPresent(Column.class) || method.isAnnotationPresent(JoinColumn.class)) {
				list.add(new ExportableColumn(m, method.getName()));
			}
		}
		return list;
	}

	public ExportableColumn[] getExportableColumns() {
		return (ExportableColumn[]) exportableColumns;
	}

	public void setExportableColumns(Object[] exportableColumns) {
		this.exportableColumns = exportableColumns;
	}

	public M getObject() {
		return object;
	}

	public void setObject(M obj) {
		object = obj;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getPanelHeader() {
		return panelHeader;
	}

	public void setPanelHeader(String panelHeader) {
		this.panelHeader = panelHeader;
	}

	public String getPanelFooter() {
		return panelFooter;
	}

	public void setPanelFooter(String panelFooter) {
		this.panelFooter = panelFooter;
	}

	public Session getSession() {
		return session;
	}

	public Integer getObjectsSize() {
		if (lazy) {
			getDataModel();
			return objectsSize;
		}
		else {
			if (objectsSize == null || objectsSize == 0)
				objectsSize = getObjects().size();
			return objectsSize;
		}
	}

	public Integer getDETAILPANEL() {
		return DETAILPANEL;
	}

	public Integer getINDEXPANEL() {
		return INDEXPANEL;
	}

	public Integer getINSERTPANEL() {
		return INSERTPANEL;
	}

	public Integer getEXPORTPANEL() {
		return EXPORTPANEL;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	public Boolean getLazy() {
		return lazy;
	}

	public void setLazy(Boolean lazy) {
		this.lazy = lazy;
	}

	public Integer getCurrentPanel() {
		return currentPanel;
	}

	public void setCurrentPanel(Integer currentPanel) {
		if (this.currentPanel != currentPanel) {
			previousPanel = this.currentPanel;
			this.currentPanel = currentPanel;
		}
	}

	public void previousPanel() {
		if (currentPanel == INSERTPANEL && previousPanel == DETAILPANEL)
			currentPanel = DETAILPANEL;
		else
			currentPanel = INDEXPANEL;
	}

	public class AbstractLazyModel extends LazyDataModel<M> implements Serializable {

		private Disjunction disjunction;

		public AbstractLazyModel() {
			Criteria criteria = getSession().createCriteria(objectClass);
			Integer rows = (Integer) criteria.setProjection(Projections.rowCount()).uniqueResult();
			if (rows != null)
				setRowCount(rows);
			else
				setRowCount(0);
			objectsSize = getRowCount();
		}

		public AbstractLazyModel(Disjunction d) {
			Criteria criteria = getSession().createCriteria(objectClass);
			if (d != null) {
				criteria.add(d);
				disjunction = d;
			}
			Integer rows = (Integer) criteria.setProjection(Projections.rowCount()).uniqueResult();
			if (rows != null)
				setRowCount(rows);
			else
				setRowCount(0);
			objectsSize = getRowCount();
		}

		@Override
		public List<M> load(int first, int pageSize, String sortField, boolean sortOrder, Map<String, String> filters) {
			Criteria criteria = getSession().createCriteria(objectClass);
			if (first > 0)
				criteria.setFirstResult(first);
			if (pageSize > 0)
				criteria.setMaxResults(pageSize);
			if (sortField != null)
				criteria.addOrder(Order.asc(sortField));
			for (String key : filters.keySet()) {
				/**
				 * TODO
				 *
				 * do the filters in the criteria
				 */
			}
			if (disjunction != null)
				criteria.add(disjunction);
			List<M> lazy = criteria.list();
			return lazy;
		}
	}
}
