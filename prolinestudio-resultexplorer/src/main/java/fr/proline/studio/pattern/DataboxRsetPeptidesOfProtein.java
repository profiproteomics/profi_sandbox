package fr.proline.studio.pattern;



import fr.proline.core.orm.msi.Peptide;
import fr.proline.core.orm.msi.ResultSet;
import fr.proline.core.orm.msi.dto.DPeptideMatch;
import fr.proline.core.orm.msi.dto.DProteinMatch;
import fr.proline.studio.extendedtablemodel.GlobalTabelModelProviderInterface;
import fr.proline.studio.dam.AccessDatabaseThread;
import fr.proline.studio.dam.tasks.AbstractDatabaseCallback;
import fr.proline.studio.dam.tasks.DatabaseLoadPeptideMatchTask;
import fr.proline.studio.dam.tasks.SubTask;
import fr.proline.studio.graphics.CrossSelectionInterface;
import fr.proline.studio.rsmexplorer.gui.PeptideMatchPanel;
import fr.proline.studio.extendedtablemodel.ExtendedTableModelInterface;

/**
 * Databox : Peptides of a Protein in a Rset
 * @author JM235353
 */
public class DataboxRsetPeptidesOfProtein extends AbstractDataBox {

    private long m_proteinMatchCurId = -1;
    
    private boolean m_mergedData;
    
            
    public DataboxRsetPeptidesOfProtein() {
        this(false);
    }
    
    public DataboxRsetPeptidesOfProtein(boolean mergedData) {
        super(DataboxType.DataboxRsetPeptidesOfProtein, DataboxStyle.STYLE_RSET);
        
        m_mergedData = mergedData;
        
        // Name of this databox
        m_typeName = "Peptides";
        m_description = "All Peptides of a Protein Match";

        // Register Possible in parameters
        // One ProteinMatch AND one ResultSet
        GroupParameter inParameter = new GroupParameter();
        inParameter.addParameter(DProteinMatch.class, false);
        inParameter.addParameter(ResultSet.class, false);
        registerInParameter(inParameter);

        // Register possible out parameters
        // One or Multiple  PeptideInstance
        GroupParameter outParameter = new GroupParameter();
        outParameter.addParameter(DPeptideMatch.class, true);
        registerOutParameter(outParameter);

        outParameter = new GroupParameter();
        outParameter.addParameter(ExtendedTableModelInterface.class, true);
        registerOutParameter(outParameter);

    }
    
    @Override
    public void createPanel() {
        PeptideMatchPanel p = new PeptideMatchPanel(false, m_mergedData, false, false, false);
        p.setName(m_typeName);
        p.setDataBox(this);
        setDataBoxPanelInterface(p);
    }

    @Override
    public void dataChanged() {
        

        final DProteinMatch proteinMatch = (DProteinMatch) m_previousDataBox.getData(false, DProteinMatch.class);
        final ResultSet rset = (ResultSet) m_previousDataBox.getData(false, ResultSet.class);

        if (proteinMatch == null) {
            ((PeptideMatchPanel) getDataBoxPanelInterface()).setData(-1, null, null, true);
             m_proteinMatchCurId = -1;
            return;
        }
        
        if ((m_proteinMatchCurId!=-1) && (proteinMatch.getId() == m_proteinMatchCurId)) {
            return;
        }
        m_proteinMatchCurId = proteinMatch.getId();
        

        final int loadingId = setLoading();
        
        // prepare callback to view new data
        AbstractDatabaseCallback callback = new AbstractDatabaseCallback() {

            @Override
            public boolean mustBeCalledInAWT() {
                return true;
            }

            @Override
            public void run(boolean success, long taskId, SubTask subTask, boolean finished) {

               if (subTask == null) {

                    DPeptideMatch[] peptideMatchArray = proteinMatch.getPeptideMatches();
                    long[] peptideMatchIdArray = proteinMatch.getPeptideMatchesId();
                    ((PeptideMatchPanel)getDataBoxPanelInterface()).setData(taskId, peptideMatchArray, peptideMatchIdArray, finished);
               } else {
                    ((PeptideMatchPanel)getDataBoxPanelInterface()).dataUpdated(subTask, finished);
               }
                
                setLoaded(loadingId);
                
                if (finished) {
                    unregisterTask(taskId);
                    propagateDataChanged(ExtendedTableModelInterface.class);
                }
                
            }
        };

        // ask asynchronous loading of data
        DatabaseLoadPeptideMatchTask task = new DatabaseLoadPeptideMatchTask(callback, getProjectId(), rset, proteinMatch);
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
        if (parameterType!= null) {
            if (parameterType.equals(DPeptideMatch.class)) {
                return ((PeptideMatchPanel)getDataBoxPanelInterface()).getSelectedPeptideMatch();
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
    public Class[] getImportantInParameterClass() {
        Class[] classList = {DPeptideMatch.class};
        return classList;
    }

    @Override
    public String getImportantOutParameterValue() {
        DPeptideMatch p = (DPeptideMatch) getData(false, DPeptideMatch.class);
        if (p != null) {
            Peptide peptide = p.getPeptide();
            if (peptide != null) {
                return peptide.getSequence();
            }
        }
        return null;
    }
    
}
