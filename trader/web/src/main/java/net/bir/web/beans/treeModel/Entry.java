package net.bir.web.beans.treeModel;

public abstract class Entry {
	
	protected String id;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	protected String name;
	protected Entry parent;
	
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
	
	public String toString() {
		return getName()+" ("+ getId()+")"; //this.getClass().getSimpleName() + "[" + name + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	
	public abstract void addEntry(Entry entry);

	public abstract void removeEntry(Entry entry);
}
