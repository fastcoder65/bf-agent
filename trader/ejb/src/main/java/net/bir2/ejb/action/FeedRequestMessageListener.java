package net.bir2.ejb.action;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import net.bir2.ejb.session.market.DataFeedService;
//import org.apache.log4j.Logger;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;


@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "/jms/queue/FeedRequestQueue") })

@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)	
public class FeedRequestMessageListener implements MessageListener {
	public static final String ACTION_PROPERTY = "action";
/*
	protected static final Logger LOG = Logger
			.getLogger(FeedRequestMessageListener.class);
*/
    @Inject
    private Logger log;

	
    @EJB
	private DataFeedService dataFeedService;

	public DataFeedService getDataFeedService() {
		return dataFeedService;
	}

	private void loadFeeds() {
		dataFeedService.readActiveDataFeeds();
	}

	private void processActionRequest(MapMessage message) throws JMSException {
		String act_name = message.getString(ACTION_PROPERTY);

		if (act_name == null) {
			log.severe("����������� ��������.");
			return;
		}

		Action act = Action.valueOf(act_name);
		log.info("given message with action: " + act.toString());

		try {
			switch (act) {
			case READ_FEEDS: {
				loadFeeds();
				break;
			}

			default: {
				log.severe("���������������� ��������: " + act.name());
			}
			}
		} catch (Throwable t) {
			log.log(Level.SEVERE, "error occured", t);
		}
	}

	public void onMessage(Message msg) {

		if (msg == null || !(msg instanceof MapMessage))
			return;

		try {

			MapMessage message = (MapMessage) msg;
			if (message.getString(ACTION_PROPERTY) != null) {
				processActionRequest(message);
            }
		} catch (Exception e) {
			log.log(Level.SEVERE, "error occured", e);
		}
	}

}
