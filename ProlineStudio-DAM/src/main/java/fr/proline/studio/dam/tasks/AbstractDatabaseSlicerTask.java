package fr.proline.studio.dam.tasks;


import fr.proline.studio.dam.taskinfo.TaskInfo;
import javax.swing.SwingUtilities;

/**
 *
 * Extend this class to write a Task which can be sliced in SubTasks
 * 
 * @author JM235353
 */
public abstract class AbstractDatabaseSlicerTask extends AbstractDatabaseTask {
    
    // Manager of the subtasks
    protected SubTaskManager m_subTaskManager;
    
    public AbstractDatabaseSlicerTask(AbstractDatabaseCallback callback) {
        super(callback, null);
    }
    
    public AbstractDatabaseSlicerTask(AbstractDatabaseCallback callback, int subTaskCount, TaskInfo taskInfo) {
        super(callback, taskInfo);
        m_subTaskManager = new SubTaskManager(subTaskCount);
    }
    public AbstractDatabaseSlicerTask(AbstractDatabaseCallback callback, int subTaskCount, Priority priority, TaskInfo taskInfo) {
        super(callback, priority, taskInfo);
        m_subTaskManager = new SubTaskManager(subTaskCount);
    }
    
    public void init(int subTaskCount, TaskInfo taskInfo) {
        setTaskInfo(taskInfo);
        m_subTaskManager = new SubTaskManager(subTaskCount);
    }
    
    @Override
    public void deleteThis() {
        super.deleteThis();
        m_subTaskManager.deleteThis();
    }
    
    /**
     * Return if there are remaining Sub Tasks to be done
     * @return 
     */
    @Override
    public boolean hasSubTasksToBeDone() {
        return !m_subTaskManager.isEmpty();
    }
    

    @Override
    public void applyPriorityChangement(PriorityChangement priorityChangement) {
        super.applyPriorityChangement(priorityChangement);
        
        m_subTaskManager.givePriorityTo(priorityChangement.getSubTaskId(), priorityChangement.getStartIndex(), priorityChangement.getStopIndex());
        
    }

    
    @Override
    public void resetPriority() {
        m_currentPriority = m_defaultPriority;
        m_subTaskManager.resetPriority();
    }
    
    
    /**
     * Method called after the data has been fetched
     * @param success  boolean indicating if the fetch has succeeded
     */
    @Override
    public void callback(final boolean success, final boolean finished) {
        if (m_callback == null) {
            return;
        }

        final SubTask taskDone = m_subTaskManager.getCurrentTask();
        /*if (taskDone != null) {
            taskDone.setAllSubtaskFinished(!hasSubTasksToBeDone());
        }*/
        
        if (m_callback.mustBeCalledInAWT()) {
            // Callback must be executed in the Graphical thread (AWT)
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    if (m_callback != null) {
                        m_callback.run(success, m_id, taskDone, finished);
                    }
                }
            });
        } else {
            // Method called in the current thread
            // In this case, we assume the execution is fast.
            m_callback.run(success, m_id, taskDone, finished);
        }


    }
    
}
