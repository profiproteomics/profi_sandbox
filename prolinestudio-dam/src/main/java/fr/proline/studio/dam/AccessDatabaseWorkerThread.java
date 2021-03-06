package fr.proline.studio.dam;

import fr.proline.studio.dam.tasks.AbstractDatabaseTask;
import org.slf4j.LoggerFactory;

/**
 * Thread which executes a task
 * @author JM235353
 */
public class AccessDatabaseWorkerThread extends Thread {
    
    private AbstractDatabaseTask m_action = null;
    
    private static int m_threadCounter = 0;
    
    private AccessDatabaseWorkerPool m_workerPool = null;
    
    public AccessDatabaseWorkerThread(AccessDatabaseWorkerPool workerPool) {
        super("AccessDatabaseWorkerThread"+m_threadCounter);
        m_threadCounter++;
        
        m_workerPool = workerPool;

    }
    
    public synchronized boolean isAvailable() {
        return (m_action == null);
    }
    
    public synchronized void setAction(AbstractDatabaseTask action) {

        m_action = action;
        notifyAll();

    }
    
    @Override
    public void run() {
        try {
            while (true) {
                
                AbstractDatabaseTask action = null;
                
                synchronized (this) {

                    while (true) {

                        if (m_action != null) {
                            action = m_action;
                            break;
                        }
                        wait();
                    }
                    notifyAll();
                }

                action.getTaskInfo().setRunning(true);

                
                // fetch data
                boolean success = action.fetchData();

                // call callback code (if there is not a consecutive task)
                action.callback(success, !action.hasSubTasksToBeDone());

                
                AccessDatabaseThread.getAccessDatabaseThread().actionDone(action);

                synchronized(this) {
                    m_action = null;
                }

                m_workerPool.threadFinished();
            }


        } catch (Throwable t) {
            LoggerFactory.getLogger("ProlineStudio.DAM").debug("Unexpected exception in main loop of AccessDatabaseWorkerThread", t);
        }

    }
    
}
