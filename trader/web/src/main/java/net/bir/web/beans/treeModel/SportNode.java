package net.bir.web.beans.treeModel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.bir.web.beans.MarketBean;
import net.bir.web.beans.treeModel.EventNode.StringKey;

import org.richfaces.model.TreeNode;

import com.betfair.aping.entities.EventType;


// @SuppressWarnings("rawtypes")
public class SportNode extends Entry implements TreeNode<EventType> {

	private static final long serialVersionUID = 1L;
	private TreeNode<EventType> parent;

	public SportNode(String id, String name) {
		this.id = id;
		this.name = name;
	}

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
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
		name = (this.eventType == null ? name : this.eventType.getName());
		return name;
	}

	public SportNode(EventType eventType) {
		this.setId(eventType.getId());
		System.out.println("SportNode construct -eventType.getName() " + eventType.getName());
		this.setName(eventType.getName());
		
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
		// System.out.println ("eventTypes.size()=" + eventTypes.size());
		return eventTypes;
	}

	public void setEventTypes(Map<Object, TreeNode> eventTypes) {
		this.eventTypes = eventTypes;
	}

	private transient Map<Object, TreeNode> eventTypes;
	{
		eventTypes = new HashMap<Object, TreeNode>();
	}

	private transient Map<Object, TreeNode> events;
	{
		events = new LinkedHashMap<Object, TreeNode>();
	}

	public Map<Object, TreeNode> getEvents() {
		return events;
	}

	public void setEvents(Map<Object, TreeNode> events) {
		this.events = events;
	}

	private transient Map<Object, TreeNode> groups;
	{
		groups = new LinkedHashMap<Object, TreeNode>();
	}

	public Map<Object, TreeNode> getGroups() {
		System.out.println("getGroups - groups.size()" + groups.size());
		return groups;
	}

	public void setGroups(Map<Object, TreeNode> groups) {
		this.groups = groups;
	}


	/*
	 * @Override public void addChild(Object identifier, TreeNode child) {
	 * events.put(identifier, child); }
	 */
	
	public void addChild(Object id, TreeNode child) {
		// System.out.println("child: " + child);

		if (child instanceof EventNode) {
			events.put(id, child);
			System.out.println("addChild - events.size(): " + events.size());
		}
		if (child instanceof SportNode) {
			eventTypes.put(id, child);
			System.out.println("addChild - eventTypes.size(): " + eventTypes.size());
		}

	}

	public TreeNode getChild(Object id) {
		return events.get(id);
	}

	public Iterator getChildren() {
		return events.entrySet().iterator();
	}

	public EventType getData() {
		return eventType;
	}

	public void setData(EventType evtType) {
		this.eventType = evtType;
	}
	
	@Override
	public SportNode getParent() {
		return (SportNode) parent;
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
		event.setParent((TreeNode) this);
	}

	public void addSport(SportNode sport) {
		System.out.println("sport: " + sport);
		addChild(new StringKey(MarketBean.NT_SPORT + sport.getId()), sport);
		sport.setParent((TreeNode) this);
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
	 * @Override public String toString() { return getName(); }
	 */

}
