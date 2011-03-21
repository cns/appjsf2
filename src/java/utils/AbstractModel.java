package utils;

import java.io.Serializable;

public abstract class AbstractModel implements Serializable {
	public abstract Integer getIdTable();
	public abstract Integer getId();
}