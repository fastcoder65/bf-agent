package net.bir.web.beans.treeModel;

import com.betfair.aping.entities.Competition;
import com.betfair.aping.entities.Event;
import com.google.common.collect.Iterators;

import javax.swing.tree.TreeNode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class EventNode extends Entry implements TreeNode {

	private static final long serialVersionUID = -7742007242946255970L;

	private TreeNode parent;

	private Competition competition;

	public Competition getCompetition() {
		return competition;
	}

	public void setCompetition(Competition competition) {
		this.competition = competition;
	}

	private Event event;

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public EventNode() {
		System.out.println("default constructor for EventNode: " + this);
	}

	public EventNode(Competition competition) {
		id   = competition.getId();
		name = competition.getName();
		this.competition = competition;
		type = "competition";
//		System.out.println("" + this);
	}

	public EventNode(Event event) {
	//	System.out.println("with arg event: " + event.getName());
		
		id   = event.getId();
		name = event.getName();
		this.event = event;
		type = "event";
//		System.out.println("" + this);
	}

	public boolean isMarketNode() {
		boolean b =  "event".equals(this.type);
//		System.out.println("isMarketNode(): " + b);
		return b;
	}

	public EventNode(Event event, TreeNode parent) {
		this(event);
		this.parent = parent;
		System.out.println("EventNode() with parent: " + parent + ", " + this);
	}
	
	public static final DateFormat df = new SimpleDateFormat("HH:mm");

	private List<TreeNode> events = new ArrayList<TreeNode>();


	public List<TreeNode> getEvents() {
/*
		if (events != null) {
		 for (TreeNode tnode: events) {
			System.out.println("eventNode.getEvents(): " + tnode); 
		 }
		}
*/
		return events;
	}

	public void setEvents(List<TreeNode> events) {
		this.events = events;
	}

	private List<TreeNode> markets = new ArrayList<TreeNode>();

	public List<TreeNode> getMarkets() {
		return markets;
	}

	public void setMarkets(List<TreeNode> markets) {
		this.markets = markets;
	}

	public Object getData() {
		return this;
	}

	@Override
	public EventNode getParent() {
		return (EventNode) parent;
	}

	public boolean isLeaf() {
		return events.isEmpty() && markets.isEmpty();
	}

	public void removeChild(Object id) {
		events.remove(id);
	}

	public void setData(Object data) {
	}

	public void setParent(TreeNode node) {
		this.parent = node;
	}

	public void addEvent(EventNode eventNode) {
		eventNode.setParent((TreeNode) this);
		events.add(eventNode);
	}

	@Override
	public String toString() {
		return "EventNode{" +
				"competition=" + competition +
				", event=" + event +
				"} " + super.toString();
	}

	public void addMarket(MarketNode marketNode) {
		marketNode.setParent((TreeNode) this);
		markets.add(marketNode);
	}

	@Override
	public void addEntry(Entry entry) {
		if (entry instanceof EventNode) {
			addEvent((EventNode) entry);
		} else {
			addMarket((MarketNode) entry);
		}
	}

	@Override
	public void removeEntry(Entry entry) {

	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		TreeNode result = null;
		
		if(!events.isEmpty())
		result = events.get(childIndex);

		if(!markets.isEmpty())
		result = markets.get(childIndex);

		return result;
	}

	public List<TreeNode> getChildren() {
	 return events;
	}

	@Override
	public int getChildCount() {
		int result = 0;
		
		if(!events.isEmpty())
		result = events.size();

		if(!markets.isEmpty())
		result = markets.size();

		return result;
	}

	@Override
	public int getIndex(TreeNode node) {
		int result = 0;
		
		if(!events.isEmpty())
		result = events.indexOf(node);

		if(!markets.isEmpty())
		result = markets.indexOf(node);

		return result;
		
	}

	@Override
	public boolean getAllowsChildren() {
		return (!events.isEmpty() || markets.isEmpty());
	}

	@Override
	public Enumeration<TreeNode> children() {
		Enumeration<TreeNode> result = null;

		if (events.size() > 0)
			result = Iterators.asEnumeration(events.iterator());

		if (markets.size() > 0)
			result = Iterators.asEnumeration(markets.iterator());
		
	 return result;	
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((event == null) ? 0 : event.hashCode());
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
		EventNode other = (EventNode) obj;
		if (event == null) {
			if (other.event != null)
				return false;
		} else if (!event.equals(other.event))
			return false;
		return true;
	}

	

}
