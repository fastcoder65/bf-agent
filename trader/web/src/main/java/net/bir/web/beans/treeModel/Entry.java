package net.bir.web.beans.treeModel;

import javax.swing.tree.TreeNode;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

public abstract class Entry implements Serializable {
	
	private static final long serialVersionUID = 1L;

	protected final Logger log = Logger.getLogger(this.getClass().getName());

	protected String id;
	protected String name;
	protected Entry parent;
	protected String type;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPath() {
		StringBuffer result = new StringBuffer();
		
		Entry parent = getParent();
		if (parent != null) {
			result.append(parent.getPath());
			result.append(" -> ");
		}

		result.append(this.toString());
		
		return result.toString();
	}
	

	@Override
	public String toString() {
		return " Entry [id=" + id + ", name=" + name + ", parent=" + parent
				+ ", type=" + type + "]";
	}

	public String getName() {
		// System.out.println("(" + this.getClass().getName() + ") getName( " + name + " )");
		return name;
	}

	public boolean isMarketNode() {
		return false;
	}

	public void setName(String _name) {
		this.name = _name;
	}

	public Entry getParent() {
		return parent;
	}

	public void setParent(Entry parent) {
		this.parent = parent;
	}
	
	public void click() {
		System.out.println("Entry.click() " + getPath());
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entry other = (Entry) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	public abstract void addEntry(Entry entry);

	public abstract void removeEntry(Entry entry);

	public abstract List<TreeNode> getChildren();

	//public abstract List<TreeNode> getEvents();

	//public abstract List<TreeNode> getMarkets();

}
