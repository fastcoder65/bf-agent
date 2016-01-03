package net.bir2.multitrade.ejb.scheduler;

import java.util.logging.Logger;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

@Singleton
public class BirSheduler {

	public static final String loadRequestQueue = "/jms/queue/PriceRequestQueue";
	
    @Inject
    private Logger log;

	private void sendRequest(String action) {

		log.fine("send  JMS \"" + action + "\" to queue "
				+ loadRequestQueue);

		javax.jms.Connection jmsConnection = null;

		Session session = null;

		MessageProducer producer = null;

		try {

			InitialContext ctx = new InitialContext();

			Queue queue = (Queue) ctx

			.lookup(loadRequestQueue);

			QueueConnectionFactory qcf = (QueueConnectionFactory) ctx

					.lookup("ConnectionFactory");


			jmsConnection = qcf.createConnection();


			session = jmsConnection.createSession(false,

			Session.AUTO_ACKNOWLEDGE);


			producer = session.createProducer(queue);


			producer.setTimeToLive(10 * 1000); // 1 sec

			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);


			MapMessage message = session.createMapMessage();

			message.setString("action", action);


			producer.send(message);

		} catch (JMSException jmse) {

			System.out.println("error while  sending message: "
					+ jmse.getMessage());

		} catch (NameNotFoundException e) {

		//	System.out.println("queue not found: " + loadRequestQueue);

		} catch (NamingException e) {

			System.out.println("Naming Exception: "
					+ e.getMessage());

		} finally {

			try {

				if (producer != null) {

					producer.close();

				}

				if (session != null) {

					session.close();

				}

				if (jmsConnection != null) {

					jmsConnection.close();

				}

			} catch (JMSException jmse) {

				System.out.println("Error on closing resources: "
						+ jmse.getMessage());

			}
		}
	}


	@Schedule(second = "*/1", minute = "*", hour = "*", info="UPDATE_MARKETS", persistent = false)
	public void perform() {
	//	System.out.println ("perform in 1 sec!");
		sendRequest("LOAD_ACTIVE_USERS");
		sendRequest("LOAD_ACTIVE_MARKETS");
		sendRequest("UPDATE_MARKETS");
	}




	@Schedule( minute = "*/15", hour = "*", info="KEEP_ALIVE", persistent = false)
	public void performKeepAlive() {
	//	System.out.println ("perform in 15 min!");
		 sendRequest("KEEP_ALIVE");
	}

}
