package fr.proline.studio.pattern;

import fr.proline.core.orm.msi.PeptideMatch;
import fr.proline.core.orm.msi.ProteinMatch;
import fr.proline.studio.dam.AccessDatabaseThread;
import fr.proline.studio.dam.tasks.AbstractDatabaseCallback;
import fr.proline.studio.dam.tasks.DatabaseProteinsFromPeptideMatchTask;
import fr.proline.studio.dam.tasks.SubTask;
import fr.proline.studio.rsmexplorer.gui.RsetProteinsForPeptideMatchPanel;

/**
 *
 * @author JM235353
 */
public class DataBoxRsetProteinsForPeptideMatch extends AbstractDataBox {
    
    private long m_peptideMatchCurId = -1;

    public DataBoxRsetProteinsForPeptideMatch() {

         // Name of this databox
        name = "Proteins";
        description = "Proteins for a Peptide Match";
        
        // Register Possible in parameters
        // One PeptideMatch
        GroupParameter inParameter = new GroupParameter();
        inParameter.addParameter(PeptideMatch.class, false);
        registerInParameter(inParameter);
        
        // Register possible out parameters
        // One or Multiple ProteinMatch
        GroupParameter outParameter = new GroupParameter();
        outParameter.addParameter(ProteinMatch.class, true);
        registerOutParameter(outParameter);

       
    }

    
    @Override
    public void createPanel() {
        RsetProteinsForPeptideMatchPanel p = new RsetProteinsForPeptideMatchPanel();
        p.setName(name);
        p.setDataBox(this);
        m_panel = p;
    }
    

    @Override
    public void dataChanged(Class dataType) {
        final PeptideMatch peptideMatch = (PeptideMatch) previousDataBox.getData(false, PeptideMatch.class);

        if (peptideMatch == null) {
            ((RsetProteinsForPeptideMatchPanel)m_panel).setData(null);
            m_peptideMatchCurId = -1;
            return;
        }

        if ((m_peptideMatchCurId!=-1) && (peptideMatch.getId() == m_peptideMatchCurId)) {
            return;
        }
        m_peptideMatchCurId = peptideMatch.getId();
        
        final int loadingId = setLoading();
        
        //final String searchedText = searchTextBeingDone; //JPM.TODO
        AbstractDatabaseCallback callback = new AbstractDatabaseCallback() {

            @Override
            public boolean mustBeCalledInAWT() {
                return true;
            }

            @Override
            public void run(boolean success, long taskId, SubTask subTask, boolean finished) {


                ((RsetProteinsForPeptideMatchPanel)m_panel).setData(peptideMatch);
                
                setLoaded(loadingId);
            }
        };

        // Load data if needed asynchronously
        DatabaseProteinsFromPeptideMatchTask task = new DatabaseProteinsFromPeptideMatchTask(callback, getProjectId(), peptideMatch);
        Long taskId = task.getId();
        if (m_previousTaskId != null) {
            // old task is suppressed if it has not been already done
            AccessDatabaseThread.getAccessDatabaseThread().removeTask(m_previousTaskId);
        }
        m_previousTaskId = taskId;
        AccessDatabaseThread.getAccessDatabaseThread().addTask(task);

    }
    private Long m_previousTaskId = null;
    
    
    @Override
    public Object getData(boolean getArray, Class parameterType) {
        if (parameterType!= null && (parameterType.equals(ProteinMatch.class))) {
            return ((RsetProteinsForPeptideMatchPanel)m_panel).getSelectedProteinMatch();
        }
        return super.getData(getArray, parameterType);
    }
}
