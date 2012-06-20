package org.openXpertya.replication;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.openXpertya.cc.CurrentAccountConnection;
import org.openXpertya.model.MReplicationHost;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.Msg;

public class ReplicationTargetProcess extends SvrProcess {

	private static final String EVTQUEUE_JNDI_NAME = "queue/EventQueue";
	
	@Override
	protected String doIt() throws Exception {

	    String destName = null;
        Context jndiContext = null;
        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        Destination dest = null;
        MessageConsumer consumer = null;
        TextMessage message = null;

        destName = EVTQUEUE_JNDI_NAME;
        // jndiContext = new InitialContext();

        CurrentAccountConnection aconn = new CurrentAccountConnection(getCtx(), get_TrxName());
        ConnectionFactory aconnFact = aconn.getConnectionFactory(MReplicationHost.getHostForOrg(1010053, get_TrxName()), MReplicationHost.getPortForOrg(1010053, get_TrxName()));
        
        jndiContext = aconn.getContext(MReplicationHost.getHostForOrg(1010053, get_TrxName()), MReplicationHost.getPortForOrg(1010053, get_TrxName()));
        
        connectionFactory = aconnFact;
        dest = (Destination) jndiContext.lookup(destName);


        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        consumer = session.createConsumer(dest);
        connection.start();

        boolean ok = true;
        while (ok) {
        	Message m = consumer.receive(1);

            if (m != null) {
            	if (m instanceof TextMessage) {
                	message = (TextMessage) m;
                	System.out.println("Reading message: " + message.getText());
                    } else {
                        break;
                    }
            }
            else ok = false;
        }
            
        connection.close();

		return "OK";
		
	}

	@Override
	protected void prepare() {
		// TODO Auto-generated method stub

	}
	
	
	
	protected Context getJNDIContext(Properties ctx, String trxName, boolean throwIfNull, int orgID) throws Exception
	{
		ReplicationConnection conn = new ReplicationConnection(orgID, trxName);
		Context context = conn.getContext();
		if(context == null && throwIfNull){
			throw new Exception(Msg.getMsg(ctx, "NoConnectionToCentral"));
		}
		return context;
	}

}
