package fr.proline.studio.pattern;


import fr.proline.core.orm.msi.PeptideInstance;
import fr.proline.core.orm.msi.ResultSummary;
import fr.proline.core.orm.msi.dto.DProteinMatch;
import fr.proline.core.orm.msi.dto.DProteinSet;
import fr.proline.studio.extendedtablemodel.GlobalTabelModelProviderInterface;
import fr.proline.studio.dam.AccessDatabaseThread;
import fr.proline.studio.dam.tasks.AbstractDatabaseCallback;
import fr.proline.studio.dam.tasks.DatabaseProteinSetsTask;
import fr.proline.studio.dam.tasks.SubTask;
import fr.proline.studio.graphics.CrossSelectionInterface;
import fr.proline.studio.rsmexplorer.gui.RsmProteinSetPanel;
import fr.proline.studio.extendedtablemodel.ExtendedTableModelInterface;


/**
 * Databox for the list of Protein Sets corresponding to a Peptide Instance
 * @author JM235353
 */
public class DataBoxRsmProteinSetOfPeptides extends AbstractDataBox {
    
    private ResultSummary m_rsm = null;

    private long m_peptideCurId = -1;
    
    public DataBoxRsmProteinSetOfPeptides() { 
        super(DataboxType.DataBoxRsmProteinSetOfPeptides, DataboxStyle.STYLE_RSM);

         // Name of this databox
        m_typeName = "Protein Set";
        m_description = "All Protein Sets coresponding to a Peptide Instance";

        // Register Possible in parameters
        // One ResultSummary

        // or one PeptideInstance
        GroupParameter inParameter = new GroupParameter();
        inParameter.addParameter(PeptideInstance.class, false);
        registerInParameter(inParameter);


        // Register possible out parameters
        // One or Multiple ProteinSet
        GroupParameter outParameter = new GroupParameter();
        outParameter.addParameter(DProteinSet.class, true);
        outParameter.addParameter(ResultSummary.class, false);
        registerOutParameter(outParameter);

        outParameter = new GroupParameter();
        outParameter.addParameter(ExtendedTableModelInterface.class, true);
        registerOutParameter(outParameter);
    }


    @Override
    public void createPanel() {
        RsmProteinSetPanel p = new RsmProteinSetPanel(false);
        p.setName(m_typeName);
        p.setDataBox(this);
        setDataBoxPanelInterface(p);
    }
    
    @Override
    public void dataChanged() {

        final PeptideInstance _peptideInstance = (PeptideInstance) m_previousDataBox.getData(false, PeptideInstance.class);


        if (_peptideInstance == null) {
            ((RsmProteinSetPanel) getDataBoxPanelInterface()).setData(null, null, true);
            m_peptideCurId = -1;
            return;
        }

        if ((m_peptideCurId!=-1) && (_peptideInstance.getId() == m_peptideCurId)) {
            return;
        }
        m_peptideCurId = _peptideInstance.getId();
        
        final int loadingId = setLoading();

        AbstractDatabaseCallback callback = new AbstractDatabaseCallback() {

            @Override
            public boolean mustBeCalledInAWT() {
                return true;
            }

            @Override
            public void run(boolean success, long taskId, SubTask subTask, boolean finished) {

                if (subTask == null) {

                    DProteinSet[] proteinSetArray = _peptideInstance.getTransientData().getProteinSetArray();
                    
                    ((RsmProteinSetPanel) getDataBoxPanelInterface()).setData(taskId, proteinSetArray, finished);
                } else {
                    ((RsmProteinSetPanel) getDataBoxPanelInterface()).dataUpdated(subTask, finished);
                }

                setLoaded(loadingId);
                
                if (finished) {
                    unregisterTask(taskId);
                    propagateDataChanged(ExtendedTableModelInterface.class);
                }
            }
        };


        // ask asynchronous loading of data
        DatabaseProteinSetsTask task = new DatabaseProteinSetsTask(callback);
        task.initLoadProteinSetForPeptideInstance(getProjectId(), _peptideInstance);
        Long taskId = task.getId();
        if (m_previousTaskId != null) {
            // old task is suppressed if it has not been already done
            AccessDatabaseThread.getAccessDatabaseThread().abortTask(m_previousTaskId);
        }
        m_previousTaskId = taskId;
        registerTask(task);

    }
    private Long m_previousTaskId = null;
    
    
    
    @Override
    public Object getData(boolean getArray, Class parameterType) {
        if (parameterType!= null ) {
            if (parameterType.equals(DProteinSet.class)) {
                return ((RsmProteinSetPanel)getDataBoxPanelInterface()).getSelectedProteinSet();
            }
            if (parameterType.equals(ResultSummary.class)) {
                if (m_rsm != null) {
                    return m_rsm;
                }
            }
            if (parameterType.equals(ExtendedTableModelInterface.class)) {
                return ((GlobalTabelModelProviderInterface) getDataBoxPanelInterface()).getGlobalTableModelInterface();
            }
            if (parameterType.equals(CrossSelectionInterface.class)) {
                return ((GlobalTabelModelProviderInterface)getDataBoxPanelInterface()).getCrossSelectionInterface();
            }
        }
        return super.getData(getArray, parameterType);
    }
 
    @Override
    public void setEntryData(Object data) {
        
        getDataBoxPanelInterface().addSingleValue(data);
        
        m_rsm = (ResultSummary) data;
        dataChanged();
    }

    @Override
    public Class[] getImportantInParameterClass() {
        Class[] classList = {DProteinSet.class};
        return classList;
    }

    @Override
    public String getImportantOutParameterValue() {
        DProteinSet p = (DProteinSet) getData(false, DProteinSet.class);
        if (p != null) {
            DProteinMatch pm = p.getTypicalProteinMatch();
            if (pm != null) {
                return pm.getAccession();
            }
        }
        return null;
    }
}
