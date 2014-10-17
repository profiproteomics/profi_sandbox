package fr.proline.studio.dpm.task.jms;


import com.thetransactioncompany.jsonrpc2.*;
import fr.profi.util.StringUtils;
import fr.proline.studio.dam.taskinfo.AbstractLongTask;
import fr.proline.studio.dam.taskinfo.TaskError;
import fr.proline.studio.dam.taskinfo.TaskInfo;
import fr.proline.studio.dpm.task.util.JMSConstants;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.*;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author JM235353
 */
public abstract class AbstractJMSTask  extends AbstractLongTask implements MessageListener {
    
    public enum JMSState {
        STATE_FAILED,
        STATE_WAITING,
        STATE_DONE
    };
    
    // callback is called by the AccessServiceThread when the service is done
    protected AbstractJMSCallback m_callback;
    
    protected Session m_session = null;
    protected MessageProducer m_producer = null;
    protected MessageConsumer m_responseConsumer = null;
    protected TemporaryQueue m_replyQueue = null;
    
    protected JMSState m_currentState = null;
    
    protected int m_id;
    //protected boolean m_synchronous;
    protected TaskError m_taskError = null;
    
    protected static int m_idIncrement = 0;

    protected static final Logger m_loggerProline = LoggerFactory.getLogger("ProlineStudio.DPM.Task");
    /*protected static final Logger m_loggerWebcore = LoggerFactory.getLogger("ProlineWebCore");*/
    
    public static final String TASK_LIST_INFO = "JMS";
    

    /* To count received messages */
    public final AtomicInteger MESSAGE_COUNT_SEQUENCE = new AtomicInteger(0);

    

    
    public AbstractJMSTask(AbstractJMSCallback callback/*, boolean synchronous*/, TaskInfo taskInfo) {
        super(taskInfo);
        
        m_callback = callback;
        //m_synchronous = synchronous;
        
        m_id = m_idIncrement++;
    }
    
    /**
     * Method called by the AccessServiceThread to ask for the service to be done
     */
    public void askJMS(Connection connection) throws JMSException {

        try {
            m_id = m_idIncrement++;

            /*
             * Thread specific : Session, Producer, Consumer ...
             */
            // Step 5. Create a JMS Session (Session MUST be confined in current Thread)
            // Not transacted, AUTO_ACKNOWLEDGE
            m_session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Step 6. Create a JMS Message Producer (Producer MUST be confined in current Thread)
            m_producer = m_session.createProducer(JMSConstants.getServiceQueue());
            m_replyQueue = m_session.createTemporaryQueue();
            m_responseConsumer = m_session.createConsumer(m_replyQueue);

            m_responseConsumer.setMessageListener(this);

            m_currentState = JMSState.STATE_WAITING;

            taskRun();
        } catch (Exception ex) {
            m_loggerProline.error("Error sending JMS Message", ex);
            m_currentState = JMSState.STATE_FAILED;
            m_taskError = new TaskError(ex);
            callback(false);
        } finally {

            if (m_session != null) {
                try {
                    m_session.close();
                    m_loggerProline.info("JMS Session closed");
                } catch (Exception exClose) {
                    m_loggerProline.error("Error closing JMS Session", exClose);
                }
            }
        }
    }
    
    /**
     * Called when the task must be started.
     * @throws JMSException 
     */
    public abstract void taskRun() throws JMSException;
    
    /**
     * Called when the task is done
     * @param jmsMessage
     * @throws Exception 
     */
    public abstract void taskDone(final Message jmsMessage) throws Exception;
    
    /**
     * Method called by the ServiceStatusThread
     * to check if the service is done
     */
    public AbstractJMSTask.JMSState getJMSState() {
        return m_currentState;
    }

    
    
    @Override
    public final void onMessage(final Message jmsMessage) {
       
        m_loggerProline.info("Receiving message n° " + MESSAGE_COUNT_SEQUENCE.incrementAndGet() + " : " + formatMessage(jmsMessage));

        try {
            taskDone(jmsMessage);
        } catch (Exception e) {
            m_currentState = JMSState.STATE_FAILED;
            m_loggerProline.error("Error handling JMS Message", e);
            m_taskError = new TaskError(e);
        }
        
        if (m_currentState == JMSState.STATE_FAILED) {
            callback(false);
        } else if (m_currentState == JMSState.STATE_DONE) {
            callback(true);
        } else {
            // should never happen : state not set
             m_taskError = new TaskError("Task with State not set");
             m_currentState = JMSState.STATE_FAILED;
             callback(false);
        }

    }
    
    /**
     * Method called to know if the task is synchronous
     */
    /*public boolean isSynchronous() {
        return m_synchronous;
    }*/

    

    /**
     * Method called after the service has been done
     *
     * @param success boolean indicating if the fetch has succeeded
     */
    protected void callback(final boolean success) {
        if (m_callback == null) {
            
            getTaskInfo().setFinished(success, m_taskError, false);
            
            return;
        }

        m_callback.setTaskInfo(m_taskInfo);
        m_callback.setTaskError(m_taskError);
        
        if (m_callback.mustBeCalledInAWT()) {
            // Callback must be executed in the Graphical thread (AWT)
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    m_callback.run(success);
                    getTaskInfo().setFinished(success, m_taskError, false);
                }
            });
        } else {
            // Method called in the current thread
            // In this case, we assume the execution is fast.
            m_callback.run(success);
            getTaskInfo().setFinished(success, m_taskError, false);
        }


    }
    
    
        /**
     * Formats some Header filds, Properties and Body of the given JMS Message to print usefull debug info.
     */
    protected String formatMessage(final Message message) {

	if (message == null) {
	    throw new IllegalArgumentException("Message is null");
	}

	final StringBuilder buff = new StringBuilder(MESSAGE_BUFFER_SIZE);

	try {
	    buff.append(message.getClass().getName()).append("  ").append(message.getJMSMessageID());
	    buff.append(StringUtils.LINE_SEPARATOR);

	    buff.append(TAB).append("JMSCorrelationID ");
	    append(buff, message.getJMSCorrelationID());
	    buff.append(StringUtils.LINE_SEPARATOR);

	    buff.append(TAB).append("JMSTimestamp ")
		    .append(String.format(DATE_FORMAT, message.getJMSTimestamp()));
	    buff.append(StringUtils.LINE_SEPARATOR);

	    buff.append(TAB).append("JMSDestination ");
	    append(buff, message.getJMSDestination());
	    buff.append(StringUtils.LINE_SEPARATOR);

	    buff.append(TAB).append("JMSReplyTo ");
	    append(buff, message.getJMSReplyTo());
	    buff.append(StringUtils.LINE_SEPARATOR);

	    final Enumeration<String> nameEnum = message.getPropertyNames();

	    while (nameEnum.hasMoreElements()) {
		final String propertyName = nameEnum.nextElement();
		buff.append(TAB).append('[').append(propertyName).append("] : ");

		final String propertyValue = message.getStringProperty(propertyName);

		if (propertyValue == null) {
		    buff.append("NULL");
		} else {
		    buff.append('[').append(propertyValue).append(']');
		}

		buff.append(StringUtils.LINE_SEPARATOR);
	    }

	    if (message instanceof TextMessage) {
		buff.append(TAB).append(((TextMessage) message).getText());
	    }

	    buff.append(StringUtils.LINE_SEPARATOR);
	} catch (Exception ex) {
	    m_loggerProline.error("Error retrieving JMS Message header or content", ex);
	}

	return buff.toString();
    }
    private static final String TAB = "    ";
    private static final String DATE_FORMAT = "%td/%<tm/%<tY %<tH:%<tM:%<tS.%<tL";
    private static final int MESSAGE_BUFFER_SIZE = 2048;
    
    private static void append(final StringBuilder sb, final Object obj) {
	assert (sb != null) : "append() sb is null";

	if (obj == null) {
	    sb.append("NULL");
	} else {
	    sb.append(obj);
	}

    }  
    
    protected void traceJSONResponse(final String jsonString) throws JSONRPC2ParseException {
	final JSONRPC2Message jsonMessage = JSONRPC2Message.parse(jsonString);

	if (jsonMessage instanceof JSONRPC2Notification) {
	    final JSONRPC2Notification jsonNotification = (JSONRPC2Notification) jsonMessage;

	    System.out.println("JSON Notification method: " + jsonNotification.getMethod());

	    final Map<String, Object> namedParams = jsonNotification.getNamedParams();

	    if ((namedParams != null) && !namedParams.isEmpty()) {
		final StringBuilder buff = new StringBuilder("Params: ");

		boolean first = true;

		final Set<Map.Entry<String, Object>> entries = namedParams.entrySet();

		for (final Map.Entry<String, Object> entry : entries) {

		    if (first) {
			first = false;
		    } else {
			buff.append(" | ");
		    }

		    buff.append(entry.getKey());
		    buff.append(" : ").append(entry.getValue());
		}

		System.out.println(buff);
	    }

	} else if (jsonMessage instanceof JSONRPC2Response) {
	    final JSONRPC2Response jsonResponse = (JSONRPC2Response) jsonMessage;

	    System.out.println("JSON Response Id: " + jsonResponse.getID());

	    final JSONRPC2Error jsonError = jsonResponse.getError();

	    if (jsonError != null) {
		m_loggerProline.error("JSON Error code {}, message : \"{}\"", jsonError.getCode(), jsonError.getMessage());
		m_loggerProline.error("JSON Throwable", jsonError);
	    }

	    final Object result = jsonResponse.getResult();

	    if (result == null) {
		System.out.println("No result");
	    } else {
		System.out.println("Result :\n" + result);
	    }

	}

    }
    
}