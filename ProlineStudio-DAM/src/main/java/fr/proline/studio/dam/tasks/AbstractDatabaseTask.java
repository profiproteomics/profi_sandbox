package fr.proline.studio.dam.tasks;

import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to be extended to access to the database through the
 * AccessDatabaseThread
 *
 * @author JM235353
 */
public abstract class AbstractDatabaseTask implements Comparable<AbstractDatabaseTask> {

    // Different possible priorities of a Task
    public enum Priority {

        LOW, // for batch actions
        NORMAL_1, // most of actions
        NORMAL_2, // most of actions, but priority higher
        NORMAL_3, // most of actions, but priority higher
        HIGH_1, // actions to be done fast
        HIGH_2, // actions to be done fast, but priority higher
        HIGH_3, // actions to be done fast, but priority higher
        TOP        // action which needs to be done first
    };
    protected static final Logger logger = LoggerFactory.getLogger(AbstractDatabaseTask.class);
    // callback is called by the AccessDatabaseThread when the data is fetched
    protected AbstractDatabaseCallback callback;
    // default priority of the task
    protected Priority defaultPriority;
    // current priority of the task
    // can be higer than defaultPriority if a task
    // must be done fast because the user waits for it
    protected Priority currentPriority;
    // id of the action
    protected Long id;
    private static long idIncrement = 0;

    /**
     * Contructor
     *
     * @param callback called by the AccessDatabaseThread when the data is
     * fetched
     */
    public AbstractDatabaseTask(AbstractDatabaseCallback callback) {
        this(callback, Priority.NORMAL_1);
    }

    public AbstractDatabaseTask(AbstractDatabaseCallback callback, Priority priority) {
        this.callback = callback;
        this.defaultPriority = priority;
        this.currentPriority = priority;

        idIncrement++;
        if (idIncrement == Long.MAX_VALUE) {
            idIncrement = 0;
        }
        id = idIncrement;

    }

    /**
     * Return the id of the Task
     *
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     * Return the current priority of the Task
     *
     * @return
     */
    public Priority getCurrentPriority() {
        return currentPriority;
    }

    public void applyPriorityChangement(PriorityChangement priorityChangement) {
        incrementPriority();
    }

    public void resetPriority() {
        defaultPriority = currentPriority;
    }

    /**
     * increment slightly the priority
     */
    public void incrementPriority() {

        switch (defaultPriority) {
            case LOW:
                currentPriority = Priority.NORMAL_1;
                break;
            case NORMAL_1:
                currentPriority = Priority.NORMAL_2;
                break;
            case NORMAL_2:
                currentPriority = Priority.NORMAL_3;
                break;
            case NORMAL_3:
                currentPriority = Priority.HIGH_1;
                break;
            case HIGH_1:
                currentPriority = Priority.HIGH_2;
                break;
            case HIGH_2:
                currentPriority = Priority.HIGH_3;
                break;
            case HIGH_3:
                currentPriority = Priority.TOP;
                break;
        }
    }

    /**
     * speed up priority to a higher level
     */
    public void speedUpPriority() {

        switch (defaultPriority) {
            case LOW:
                currentPriority = Priority.NORMAL_3;
                break;
            case NORMAL_1:
                currentPriority = Priority.HIGH_1;
                break;
            case NORMAL_2:
                currentPriority = Priority.HIGH_2;
                break;
            case NORMAL_3:
                currentPriority = Priority.HIGH_3;
                break;
            case HIGH_1:
            case HIGH_2:
            case HIGH_3:
                currentPriority = Priority.TOP;
                break;
        }
    }

    /**
     * Method called by the AccessDatabaseThread to fetch Data from database
     *
     * @return
     */
    public abstract boolean fetchData();

    /**
     * Method called by the AccessDatabaseThread to check if data is or not
     * already known
     *
     * @return
     */
    public abstract boolean needToFetch();

    /**
     * Return if there are sub tasks which remain to be done later
     *
     * @return
     */
    public boolean hasSubTasksToBeDone() {
        return false;
    }

    /**
     * Method called after the data has been fetched
     *
     * @param success boolean indicating if the fetch has succeeded
     */
    public void callback(final boolean success) {
        if (callback == null) {
            return;
        }

        if (callback.mustBeCalledInAWT()) {
            // Callback must be executed in the Graphical thread (AWT)
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    callback.run(success, id, null);
                }
            });
        } else {
            // Method called in the current thread
            // In this case, we assume the execution is fast.
            callback.run(success, id, null);
        }


    }

    /**
     * Used to prioritize actions
     *
     * @param task
     * @return
     */
    @Override
    public int compareTo(AbstractDatabaseTask task) {

        // first we compare on priority
        long diff = task.currentPriority.ordinal() - currentPriority.ordinal();
        if (diff != 0) {
            return (diff) > 0 ? 1 : -1;
        }

        // for equal priority, we compare on id : priority is given to older id == smaller
        diff = id - task.id;
        if (diff == 0) {
            return 0;
        }
        return (diff) > 0 ? 1 : -1;
    }
}
