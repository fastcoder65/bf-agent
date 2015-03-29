package net.bir2.ejb.session.market;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;

import com.unitab.race.Race;

import net.bir2.multitrade.ejb.entity.DataFeedEvent;
import net.bir2.multitrade.ejb.entity.Feed4Market4User;
import net.bir2.multitrade.ejb.entity.Feed4Runner4User;
import net.bir2.multitrade.util.url.URLContentGetterException;

@Local
public interface DataFeedService {

	public String getURLContentAsString(String path)
			throws URLContentGetterException;

	public String getURLContentAsString(String path, int connectionTimeout,
			int readTimeout) throws URLContentGetterException;

	public String getHttpUrlContentAsString(String path, String data2send,
			String httpMethod) throws URLContentGetterException;

	public String getHttpUrlContentAsString(String path, String data2send,
			String httpMethod, int connectionTimeout, int readTimeout)
			throws URLContentGetterException;

	public void readActiveDataFeeds();

	

	//public boolean findUnitabDataFeedEvent(Set<String> runnerNames);
	public DataFeedEvent findUnitabDataFeedEvent(Set<String> runnerNames);
	
	public boolean isFeed4Market4UserAlreadyExists(String likeName, long marketId, int userId);
	public Feed4Market4User getFeed4Market4User(String likeName, long marketId, int userId);
	
	public boolean isFeed4Runner4UserAlreadyExists(String likeName, long runnerId, int userId);
	public Feed4Runner4User getFeed4Runner4User(String likeName, long runnerId, int userId);
	
	public Race getFreshedUnitabRace(DataFeedEvent dataFeedEvent);
	public List<DataFeedEvent> listDataFeedEvents(String sourceName, Date now);

	public DataFeedEvent getDataFeedEvent(long id);
	
}
