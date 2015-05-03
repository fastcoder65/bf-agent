package net.bir.web.beans.treeModel;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import net.bir.web.beans.MarketBean;

import org.richfaces.model.TreeNode;

import com.betfair.aping.entities.Event;

@SuppressWarnings("rawtypes")
public class EventNode extends Entry implements TreeNode {
	
	private static final long serialVersionUID = 9220676792064698125L;

	private transient  TreeNode parent;

	private transient  Event event;
	
	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public EventNode() {
	}

	public EventNode(Event event) {
		this.setId(event.getId());
		this.setName(event.getName());
		this.event = event;
	}

	public EventNode(Event event,  TreeNode parent) {
		this(event);
		this.parent = parent;
	}

	public String getId() {
		return id;
	}

	private String type= "event";
	
	public String getType() {
		return type;
	}
	
	public static final DateFormat df = new SimpleDateFormat("HH:mm");

	public String getName() {
		return name;
	}


    private transient Map<Object, TreeNode> events;{
        events = new TreeMap<Object, TreeNode>();
    }

    public Map<Object, TreeNode> getEvents() {
		return events;
	}

	public void setEvents(Map<Object, TreeNode> events) {
		this.events = events;
	}

    private transient Map<Object, TreeNode> markets;{
        markets = new TreeMap<Object, TreeNode>();
    }

    public Map<Object, TreeNode> getMarkets() {
		return markets;
	}

	public void setMarkets(Map<Object, TreeNode> events) {
		this.markets = events;
	}



	public void addChild(Object id, TreeNode child) {

	 if (child instanceof EventNode) {
		 events.put(id, child);
	 } 

	 if (child instanceof MarketNode) {
		 markets.put(id, child);
	 }

	}

	
	public TreeNode getChild(Object id) {
		return events.get(id);
	}


	public Iterator<?> getChildren() {
		return  events.entrySet().iterator();
	}


	public Object getData() {
		return event;
	}

	@Override
	public EventNode getParent() {
		return (EventNode)parent;
	}


	public boolean isLeaf() {
		  return events.isEmpty();
	}


	public void removeChild(Object id) {
		 events.remove(id);
	}

	
	public void setData(Object data) {
	}

	
	public void setParent(TreeNode node) {
		this.parent = node;
	}
	
	public void addEvent(EventNode event) {
		addChild(new StringKey(MarketBean.NT_EVENT + event.getPath()), event);
		event.setParent((TreeNode)this);
	}

	@Override
	public String toString() {
		return "EventNode [event=" + event + ", type=" + type + "]";
	}

	public void addMarket(MarketNode market) {
		String sKey = MarketBean.NT_MARKET + market.getMarket().getMarketId();
		addChild(new StringKey(sKey), market);
		market.setParent((TreeNode)this);
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
		events.remove(entry);
		markets.remove(entry);
	}

	public static class StringKey implements Serializable, Comparable<StringKey> {

		private static final long serialVersionUID = 1L;
		private String s;
	    
	    public StringKey(String s) {
		super();
		this.s = s;
	    }

	    @Override
	    public String toString() {
	        return s;
	    }

	    @Override
	    public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((s == null) ? 0 : s.hashCode());
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
		StringKey other = (StringKey) obj;
		if (s == null) {
		    if (other.s != null)
			return false;
		} else if (!s.equals(other.s))
		    return false;
		return true;
	    }

		@Override
		public int compareTo(StringKey sk) {
			return this.s.compareTo(sk.s);
		}
	}

}
