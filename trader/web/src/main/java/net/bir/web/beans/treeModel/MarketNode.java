package net.bir.web.beans.treeModel;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

// import org.richfaces.model.TreeNode;
import javax.swing.tree.TreeNode;

import com.betfair.aping.entities.MarketCatalogue;
import com.google.common.base.*;
import com.google.common.base.Objects;
import net.bir.web.beans.BaseBean;


public class MarketNode extends Entry implements TreeNode {

	private static final long serialVersionUID = 1L;
	private transient MarketCatalogue market;

	public MarketCatalogue getMarket() {
		return market;
	}

	public MarketNode() {
	}

	private static Integer timeOffset;

	private static Integer getTimeOffset() {
		if (timeOffset == null) {

			TimeZone myTimeZone = java.util.TimeZone.getDefault();

			timeOffset = myTimeZone.getOffset((new Date()).getTime());
		}
	   return timeOffset;
	}

	public MarketNode(MarketCatalogue market) {

		id = market.getMarketId();

		Calendar c = Calendar.getInstance();
		c.setTime(market.getDescription().getMarketTime());
		c.add(Calendar.MILLISECOND, getTimeOffset());

		String _marketTime = (market.getDescription() != null ? BaseBean.shortTimeFormat.format( c.getTime() )+ " " : "");

		this.name = _marketTime + market.getMarketName();

		this.market = market;
		this.type = "market";
	}

	public MarketNode(MarketCatalogue market, TreeNode parent) {
		this(market);
		this.parent = parent;
	}

	public String getId() {
		return id;
	}
	
	public static final DateFormat df = new SimpleDateFormat("HH:mm");

/*
	public String getName() {
		return this.getMarket().getMarketName();
	}
*/

	@Override
	public String toString() {
		return "MarketNode{" +
				" id="+ id +
				", name="+ name +
				", type=" + type +
				", market=" + market +
				", parent=" + parent +
				'}';
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
	public Entry getParent() {
		return (Entry) parent;
	}

	public void setParent(Entry node) {
		this.parent = node;
	}

	public List<TreeNode> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getData() {
		return market;
	}

	public boolean isLeaf() {
		// TODO Auto-generated method stub
		return true;
	}

	public void removeChild(Object arg0) {
		// TODO Auto-generated method stub

	}

	public void setData(Object arg0) {
		// TODO Auto-generated method stub

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
