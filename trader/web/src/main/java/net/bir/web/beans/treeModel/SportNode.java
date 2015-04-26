package net.bir.web.beans.treeModel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.bir.web.beans.MarketBean;
import net.bir.web.beans.treeModel.EventNode.StringKey;

import org.richfaces.model.TreeNode;

import com.betfair.aping.entities.EventType;
// import generated.global.BFGlobalServiceStub.EventType;

@SuppressWarnings("rawtypes")
public class SportNode extends Entry  implements TreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6635293645813230420L;
	private TreeNode parent;
	
	public SportNode(int id, String name) {
		this.id = id;
		this.name = name;
	}

	private int id;

	public int getId() {
		if (id == 0L)
		id = (this.eventType == null ? id : Integer.valueOf(this.eventType.getId()));
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private String type = "sport";

	public String getType() {
		return type;
	}

	private String name;

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		name = (this.eventType == null? name : this.eventType.getName());
		return name;
	}

	public SportNode(EventType eventType) {
		super();
		this.eventType = eventType;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	private transient EventType eventType;

	public Map<Object, TreeNode> getEventTypes() {
	//	System.out.println ("eventTypes.size()=" + eventTypes.size());
		return eventTypes;
	}

	public void setEventTypes(Map<Object, TreeNode> eventTypes) {
		this.eventTypes = eventTypes;
	}

    private transient Map<Object, TreeNode> eventTypes;{
        eventTypes = new HashMap<Object, TreeNode>();
    }

//	private Map<Object, TreeNode> events = new HashMap<Object, TreeNode>();

    private transient Map<Object, TreeNode> events;{
        events = new LinkedHashMap<Object, TreeNode>();
    }

    public Map<Object, TreeNode> getEvents() {
		// System.out.println("events.size()" + events.size());
		return events;
	}

	public void setEvents(Map<Object, TreeNode> events) {
		this.events = events;
	}

/*	@Override
	public void addChild(Object identifier, TreeNode child) {
		events.put(identifier, child);
	}
*/
	public void addChild(Object id, TreeNode child) {
		 if (child instanceof EventNode) {
			 events.put(id, child);
		 } 
		 if (child instanceof SportNode) {
			 eventTypes.put(id, child);
		 }
		}


	public TreeNode getChild(Object id) {
		return events.get(id);
	}


	public Iterator getChildren() {
		return events.entrySet().iterator();
	}


	public Object getData() {
		return eventType;
	}

	@Override
	public SportNode getParent() {
		return (SportNode)parent;
	}


	public boolean isLeaf() {
		return events.isEmpty();
	}


	public void removeChild(Object id) {
		events.remove(id);
	}

	
	public void setData(Object arg0) {
	}

	
	public void setParent(TreeNode node) {
		this.parent = node;

	}

   
	private transient Object state;

	public Object getState() {
		return state;
	}

	public void setState(Object state) {
		this.state = state;
	}

	public void addEvent(EventNode event) {
		addChild(new StringKey(MarketBean.NT_EVENT + event.getId()), event);
		event.setParent((TreeNode)this);
	}

	public void addSport(SportNode sport) {
		addChild(new StringKey(MarketBean.NT_SPORT + sport.getId()), sport);
		sport.setParent((TreeNode)this);
	}

	@Override
	public void addEntry(Entry entry) {
		if (entry instanceof EventNode) {
			addEvent((EventNode) entry);
		} else {
			addSport((SportNode) entry);
		}
		
	}

	@Override
	public void removeEntry(Entry entry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		return "SportNode [id=" + id + ", type=" + type + ", name=" + name
				+ "]";
	}

	/*
	@Override
	public String toString() {
		return getName();
	}
	*/

}
