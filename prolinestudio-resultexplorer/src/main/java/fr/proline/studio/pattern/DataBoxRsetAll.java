package fr.proline.studio.pattern;


import fr.proline.core.orm.msi.ResultSet;
import fr.proline.core.orm.uds.Project;
import fr.proline.studio.dam.tasks.AbstractDatabaseCallback;
import fr.proline.studio.dam.tasks.DatabaseRsetTask;
import fr.proline.studio.dam.tasks.SubTask;
import fr.proline.studio.rsmexplorer.actions.identification.ImportSearchResultAsRsetJMSAction;
import fr.proline.studio.rsmexplorer.gui.RsetAllPanel;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;

/**
 * Databox for all Rset of a project
 * @author JM235353
 */
public class DataBoxRsetAll extends AbstractDataBox {
    
    private Project m_project = null;
    
    public DataBoxRsetAll() {
        super(DataboxType.DataBoxRsetAll, DataboxStyle.STYLE_RSET);
        
        
        // Name of this databox
        m_typeName = "Search Results";
        
        // Register Possible in parameters
        // One ResultSummary
        GroupParameter inParameter = new GroupParameter();
        inParameter.addParameter(Project.class, false);
        registerInParameter(inParameter);
        
        // Register possible out parameters
        // One or Multiple PeptideMatch
        GroupParameter outParameter = new GroupParameter();
        outParameter.addParameter(ResultSet.class, true);
        registerOutParameter(outParameter);

    }

    @Override
    public void createPanel() {
        RsetAllPanel p = new RsetAllPanel();
        p.setName(m_typeName);
        p.setDataBox(this);
        setDataBoxPanelInterface(p);
        
        
    }

    @Override
    public void dataChanged() {

            Project p = (m_project != null) ? m_project : (Project) m_previousDataBox.getData(false, Project.class);

            final ArrayList<ResultSet> resultSetArrayList = new ArrayList<>();
            
            final int loadingId = setLoading();
            
            AbstractDatabaseCallback callback = new AbstractDatabaseCallback() {

                @Override
                public boolean mustBeCalledInAWT() {
                    return true;
                }

                @Override
                public void run(boolean success, long taskId, SubTask subTask, boolean finished) {

                    ((RsetAllPanel) getDataBoxPanelInterface()).setData(taskId, resultSetArrayList);

                    setLoaded(loadingId);
                    
                    if (finished) {
                        unregisterTask(taskId);
                    }
                }
            };


            // ask asynchronous loading of data
            
            DatabaseRsetTask task = new DatabaseRsetTask(callback, p.getId(), resultSetArrayList);
            registerTask(task);

    }
    
    @Override
    public Object getData(boolean getArray, Class parameterType) {
        if (parameterType!= null ) {
            if (parameterType.equals(ResultSet.class)) {
                return ((RsetAllPanel)getDataBoxPanelInterface()).getSelectedResultSet();
            }

        }
        return super.getData(getArray, parameterType);
    }
    
    @Override
    public void setEntryData(Object data) {
        
        getDataBoxPanelInterface().addSingleValue(data);
        
        m_project = (Project) data;

        dataChanged();
        
        ImportSearchResultAsRsetJMSAction.addEventListener(m_project.getId(), this);
    }

    @Override
    public void windowClosed() {
        ImportSearchResultAsRsetJMSAction.removeEventListener(m_project.getId(), this);
        super.windowClosed();
    }

    
    @Override
    public void stateChanged(ChangeEvent e) {
        dataChanged();
    }
    
}
