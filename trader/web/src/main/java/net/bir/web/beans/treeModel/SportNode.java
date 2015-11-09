package net.bir.web.beans.treeModel;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import com.betfair.aping.entities.EventType;

//import org.richfaces.model.TreeNode;

public class SportNode extends Entry implements TreeNode {

	private static final long serialVersionUID = 2252308709240494243L;

	private TreeNode parent;

	public SportNode(String id, String name) {
		this.id = id;
		this.name = name;
		setType("sport");
	}

	public SportNode(EventType eventType) {
		this.setId(eventType.getId());
		System.out.println("SportNode construct -eventType.getName() "
				+ eventType.getName());
		this.name = eventType.getName();
		this.eventType = eventType;
		type = "sport";
	}

	private EventType eventType;

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	private List<TreeNode> eventTypes = new ArrayList<TreeNode>();

	public List<TreeNode> getEventTypes() {
		return eventTypes;
	}

	public void setEventTypes(List<TreeNode> eventTypes) {
		this.eventTypes = eventTypes;
	}


	private List<TreeNode> markets = new ArrayList<TreeNode>();

	public List<TreeNode> getMarkets() {
		return markets;
	}

	public void setMarkets(List<TreeNode> markets) {
		this.markets = markets;
	}


	private List<TreeNode> events = new ArrayList<TreeNode>();

	public List<TreeNode> getEvents() {
/*
		if (events != null) {
			for (TreeNode tnode : events) {
				System.out.println("sportNode.getEvents(): " + tnode);
			}
		}
*/
		return events;
	}

	public void setEvents(List<TreeNode> events) {
		this.events = events;
	}

	private List<TreeNode> groups = new ArrayList<TreeNode>();

	public List<TreeNode> getGroups() {
		return groups;
	}

	public void setGroups(List<TreeNode> groups) {
		this.groups = groups;
	}

	/*
	 * public void addChild(Object id, TreeNode child) { //
	 * System.out.println("child: " + child);
	 * 
	 * if (child instanceof EventNode) { events.put(id, child);
	 * System.out.println("addChild - events.size(): " + events.size()); } if
	 * (child instanceof SportNode) { eventTypes.put(id, child);
	 * System.out.println("addChild - eventTypes.size(): " + eventTypes.size());
	 * }
	 * 
	 * }
	 */

	public Object getData() {
		return this;
	}

	@Override
	public SportNode getParent() {
		return (SportNode) parent;
	}

	public boolean isLeaf() {
		return events.isEmpty();
	}

	public void setParent(TreeNode node) {
		this.parent = node;
	}

	public void addEvent(EventNode eventNode) {
		eventNode.setParent((TreeNode) this);
		events.add(eventNode);
	}

	public void addSport(SportNode sportNode) {
		System.out.println("sport: " + sportNode);
		sportNode.setParent((TreeNode) this);
		eventTypes.add(sportNode);
	}

	@Override
	public void addEntry(Entry entry) {
		if (entry instanceof EventNode) {
			addEvent((EventNode) entry);
			// System.out.println("entry 'event' added : " + entry);
		} else if (entry instanceof SportNode) {
			addSport((SportNode) entry);
			System.out.println("entry 'sport' added : " + entry);
		} else {
			System.out.println("entry of unknown type found!");
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

	@Override
	public TreeNode getChildAt(int childIndex) {
		TreeNode result = null;

		if (!eventTypes.isEmpty())
			result = eventTypes.get(childIndex);

		if (!events.isEmpty())
			result = events.get(childIndex);

		if (!groups.isEmpty())
			result = groups.get(childIndex);

		return result;
	}

	@Override
	public int getChildCount() {
		int result = 0;

		if (!eventTypes.isEmpty())
			result = eventTypes.size();

		if (!events.isEmpty())
			result = events.size();

		if (!groups.isEmpty())
			result = groups.size();

		return result;
	}

	@Override
	public int getIndex(TreeNode node) {
		int result = 0;

		if (!eventTypes.isEmpty())
			result = eventTypes.indexOf(node);

		if (!events.isEmpty())
			result = events.indexOf(node);

		if (!groups.isEmpty())
			result = groups.indexOf(node);

		return result;

	}

	@Override
	public boolean getAllowsChildren() {
		return (!eventTypes.isEmpty() || !events.isEmpty() || !groups.isEmpty());
	}

	@Override
	public Enumeration<TreeNode> children() {

		Enumeration<TreeNode> result = null;
		/*
		 * if (eventTypes.size() > 0) result =
		 * Iterators.asEnumeration(eventTypes.iterator());
		 * 
		 * if (events.size() > 0) result =
		 * Iterators.asEnumeration(events.iterator());
		 * 
		 * if (groups.size() > 0) result =
		 * Iterators.asEnumeration(groups.iterator());
		 */

		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((eventType == null) ? 0 : eventType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SportNode other = (SportNode) obj;
		if (eventType == null) {
			if (other.eventType != null)
				return false;
		} else if (!eventType.equals(other.eventType))
			return false;
		return true;
	}

}
