package net.bir.web.beans.treeModel;

public abstract class Entry {
	
	private int id;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private String name;
	private Entry parent;
	
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
