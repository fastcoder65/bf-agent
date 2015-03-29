package net.bir.web.beans.treeModel;

import generated.global.BFGlobalServiceStub.MarketSummary;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Iterator;
import java.io.Serializable;

import org.richfaces.model.TreeNode;

@SuppressWarnings("rawtypes")
public class MarketNode extends Entry implements TreeNode {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2114482821117813849L;
	private transient  MarketSummary market;
	
	public MarketSummary getMarket() {
		return market;
	}

	public MarketNode() {
	}

	public MarketNode(MarketSummary market) {
		super();
		this.market = market;
	}

	public MarketNode(MarketSummary market, TreeNode parent) {
		super();
		this.market = market;
		this.parent = parent;
	}
	
	public int getId() {
	    super.setId(this.market.getMarketId());
		return super.getId();
	}

	private String type= "market";
	
	public String getType() {
		return type;
	}
	
	public static final DateFormat df = new SimpleDateFormat("HH:mm");
	
	public String getName() {
		String marketDate = (this.market.getStartTime() == null? "" : df.format(this.market.getStartTime().getTime()));
		super.setName(("03:00".equals(marketDate)?"": marketDate +  " " )+ this.market.getMarketName());
		return super.getName();
	}

	@Override
	public void addEntry(Entry entry) {
		// TODO Auto-generated method stub
	}

	@Override
	public void removeEntry(Entry entry) {
		// TODO Auto-generated method stub
		
	}

	
	public void addChild(Object arg0, TreeNode arg1) {
		// TODO Auto-generated method stub
		
	}

	
	public TreeNode getChild(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	private TreeNode parent;
	
	@Override
	public MarketNode getParent() {
		return (MarketNode)parent;
	}


	public void setParent(TreeNode arg0) {
		// TODO Auto-generated method stub

	}


	public Iterator getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Object getData() {
		return market;
	}


	public boolean isLeaf() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void removeChild(Object arg0) {
		// TODO Auto-generated method stub
		
	}


	public void setData(Object arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		return "MarketNode [market=" + market + ", parent=" + parent
				+ ", type=" + type + "]";
	}

	public static class MarketNodeComparator implements Comparator<MarketNode>, Serializable {
        private static final long serialVersionUID = 3063185261278711795L;

        public int compare(MarketNode o1, MarketNode o2) {
			return Integer.valueOf(o1.getMarket().getOrderIndex()).compareTo(o2.getMarket().getOrderIndex());
		}
	}
}
