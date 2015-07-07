package net.bir.web.beans.treeModel;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;

// import org.richfaces.model.TreeNode;
import javax.swing.tree.TreeNode;

import com.betfair.aping.entities.MarketCatalogue;


public class MarketNode extends Entry implements TreeNode {

	private static final long serialVersionUID = 1L;
	private transient MarketCatalogue market;

	public MarketCatalogue getMarket() {
		return market;
	}

	public MarketNode() {
	}

	public MarketNode(MarketCatalogue market) {
		System.out.println( market );
		id = market.getMarketId();
		name = market.getMarketName();
		this.market = market;
		type = "market";
	}

	public MarketNode(MarketCatalogue market, TreeNode parent) {
		this(market);
		this.parent = parent;
	}

	public String getId() {
		return id;
	}
	
	public static final DateFormat df = new SimpleDateFormat("HH:mm");

	public String getName() {
		return this.getMarket().getMarketName();
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
		return (MarketNode) parent;
	}

	public void setParent(TreeNode arg0) {
		// TODO Auto-generated method stub

	}

	public Iterator<TreeNode> getChildren() {
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

	public static class MarketNodeComparator implements Comparator<MarketNode>,
			Serializable {
		private static final long serialVersionUID = 1L;

		public int compare(MarketNode o1, MarketNode o2) {
			return Integer.valueOf(o1.getMarket().getMarketId()).compareTo(
					Integer.valueOf(o2.getMarket().getMarketId()));
		}
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getChildCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIndex(TreeNode node) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getAllowsChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Enumeration<TreeNode> children() {
		return null;
	}

}
