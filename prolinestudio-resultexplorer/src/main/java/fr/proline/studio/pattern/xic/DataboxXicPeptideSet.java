package fr.proline.studio.pattern.xic;

import fr.proline.core.orm.msi.Peptide;
import fr.proline.core.orm.msi.ResultSummary;
import fr.proline.core.orm.msi.dto.DMasterQuantPeptide;
import fr.proline.core.orm.msi.dto.DMasterQuantProteinSet;
import fr.proline.core.orm.msi.dto.DPeptideInstance;
import fr.proline.core.orm.msi.dto.DPeptideMatch;
import fr.proline.core.orm.msi.dto.DProteinSet;

import fr.proline.core.orm.uds.dto.DDataset;
import fr.proline.studio.extendedtablemodel.GlobalTabelModelProviderInterface;
import fr.proline.studio.dam.tasks.AbstractDatabaseCallback;
import fr.proline.studio.dam.tasks.SubTask;
import fr.proline.studio.dam.tasks.xic.DatabaseLoadLcMSTask;
import fr.proline.studio.dam.tasks.xic.DatabaseLoadXicMasterQuantTask;
import fr.proline.studio.graphics.CrossSelectionInterface;
import fr.proline.studio.pattern.AbstractDataBox;
import fr.proline.studio.pattern.GroupParameter;
import fr.proline.studio.rsmexplorer.gui.xic.QuantChannelInfo;
import fr.proline.studio.rsmexplorer.gui.xic.XicPeptidePanel;
import fr.proline.studio.types.XicMode;
import java.util.ArrayList;
import java.util.List;
import fr.proline.studio.extendedtablemodel.ExtendedTableModelInterface;
import fr.proline.studio.extendedtablemodel.SecondAxisTableModelInterface;
import fr.proline.studio.rsmexplorer.gui.xic.PeptideTableModel;
import fr.proline.studio.rsmexplorer.gui.xic.XicAbundanceProteinTableModel;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author JM235353
 */
public class DataboxXicPeptideSet extends AbstractDataBox {

    private static final Logger m_logger = LoggerFactory.getLogger("ProlineStudio.ResultExplorer");
    private DDataset m_dataset;
    private DProteinSet m_proteinSet;
    private DMasterQuantProteinSet m_masterQuantProteinSet;
    private List<DMasterQuantPeptide> m_masterQuantPeptideList;
    private QuantChannelInfo m_quantChannelInfo;

    private boolean m_isXICMode = true;
    //Display only a subList of peptides instances not all from Peptide/ProteinSet
    private boolean m_displayPeptidesSubList = false; 

    public DataboxXicPeptideSet() {
        this(false);
    }
    
    public DataboxXicPeptideSet(boolean displayPeptidesSubList) {
        super((displayPeptidesSubList ? DataboxType.DataboxXicPeptideSetShortList : DataboxType.DataboxXicPeptideSet), DataboxStyle.STYLE_XIC);
        
        m_displayPeptidesSubList = displayPeptidesSubList;

        // Name of this databox
        if(!m_displayPeptidesSubList) {
            m_typeName = "Quanti Peptides";
            m_description = "All Quanti. Peptides of a ProteinSet";
        } else {
            m_typeName = "Quanti Peptides";
            m_description = "Short list of Quanti. Peptides";            
        }

        // Register Possible in parameters
        // One Dataset and list of Peptide
        GroupParameter inParameter = new GroupParameter();
        inParameter.addParameter(DDataset.class, false);
        registerInParameter(inParameter);

        inParameter = new GroupParameter();
        inParameter.addParameter(DProteinSet.class, false);
        inParameter.addParameter(DMasterQuantProteinSet.class, false);
        inParameter.addParameter(QuantChannelInfo.class, false);
        inParameter.addParameter(XicMode.class, false);
        registerInParameter(inParameter);

        // Register possible out parameters
        GroupParameter outParameter = new GroupParameter();
        outParameter.addParameter(DMasterQuantPeptide.class, false);
        outParameter.addParameter(XicMode.class, false);
        outParameter.addParameter(DDataset.class, false);
        outParameter.addParameter(ResultSummary.class, false);
        outParameter.addParameter(QuantChannelInfo.class, false);
        outParameter.addParameter(DPeptideMatch.class, false);
        registerOutParameter(outParameter);

        outParameter = new GroupParameter();
        outParameter.addParameter(ExtendedTableModelInterface.class, true);
        registerOutParameter(outParameter);

        outParameter = new GroupParameter();
        outParameter.addParameter(CrossSelectionInterface.class, true);
        registerOutParameter(outParameter);

        outParameter = new GroupParameter();
        outParameter.addParameter(SecondAxisTableModelInterface.class, true);
        registerOutParameter(outParameter);

    }

    public boolean isXICMode() {
        return m_isXICMode;
    }

    public void setXICMode(boolean isXICMode) {
        m_isXICMode = isXICMode;
        m_style = (m_isXICMode) ? DataboxStyle.STYLE_XIC : DataboxStyle.STYLE_SC;
        if (getDataBoxPanelInterface() != null) {
            getDataBoxPanelInterface().addSingleValue(new XicMode((isXICMode)));
        }
    }

    @Override
    public Long getRsetId() {
        if (m_dataset == null) {
            return null;
        }
        return m_dataset.getResultSetId();
    }

    @Override
    public Long getRsmId() {
        if (m_dataset == null) {
            return null;
        }
        return m_dataset.getResultSummaryId();
    }

    @Override
    public void createPanel() {
        XicPeptidePanel p = new XicPeptidePanel(false, m_isXICMode);
        p.setName(m_typeName);
        p.setDataBox(this);
        setDataBoxPanelInterface(p);

        getDataBoxPanelInterface().addSingleValue(new XicMode((m_isXICMode)));
    }

    @Override
    public void dataChanged() {
        final boolean allPeptides = (m_previousDataBox == null);
        List<DPeptideInstance> pepInstances = null;
        if (!allPeptides) {
            DProteinSet newProSet = (DProteinSet) m_previousDataBox.getData(false, DProteinSet.class);
            DMasterQuantProteinSet newMasterProtSet = (DMasterQuantProteinSet) m_previousDataBox.getData(false, DMasterQuantProteinSet.class);
            DDataset newDS = (DDataset) m_previousDataBox.getData(false, DDataset.class);            ;
            
            boolean valueUnchanged  = Objects.equals(newProSet, m_proteinSet) && Objects.equals(newMasterProtSet,m_masterQuantProteinSet) && Objects.equals(newDS,m_dataset);
            if(valueUnchanged && !m_displayPeptidesSubList)
                return;
            
            m_proteinSet = newProSet;
            m_masterQuantProteinSet = newMasterProtSet;
            m_dataset = newDS;            
            if (m_proteinSet == null) {
                return;
            }
            
            m_isXICMode = ((XicMode) m_previousDataBox.getData(false, XicMode.class)).isXicMode();
            m_quantChannelInfo = (QuantChannelInfo) m_previousDataBox.getData(false, QuantChannelInfo.class);
            if(m_quantChannelInfo == null)
                throw new RuntimeException("Xic PeptideSet : Can't get QuantChannelInfo from previous databox");

            if(m_displayPeptidesSubList)
                pepInstances = (List<DPeptideInstance>) m_previousDataBox.getData(false, DPeptideInstance.class, true);
        }

        final int loadingId = setLoading();

        AbstractDatabaseCallback callback = new AbstractDatabaseCallback() {

            @Override
            public boolean mustBeCalledInAWT() {
                return true;
            }

            @Override
            public void run(boolean success, final long taskId, SubTask subTask, boolean finished) {
                if (subTask == null) {
                    if (!allPeptides && m_quantChannelInfo != null) {
                        ((XicPeptidePanel) getDataBoxPanelInterface()).setData(taskId, m_proteinSet != null, m_quantChannelInfo.getQuantChannels(), m_masterQuantPeptideList, m_isXICMode, finished);

                    } else {
                        AbstractDatabaseCallback mapCallback = new AbstractDatabaseCallback() {

                            @Override
                            public boolean mustBeCalledInAWT() {
                                return true;
                            }

                            @Override
                            public void run(boolean success, long task2Id, SubTask subTask, boolean finished) {
                                m_quantChannelInfo = new QuantChannelInfo(m_dataset);
                                getDataBoxPanelInterface().addSingleValue(m_quantChannelInfo);

                                ((XicPeptidePanel) getDataBoxPanelInterface()).setData(taskId, m_proteinSet != null, m_quantChannelInfo.getQuantChannels(), m_masterQuantPeptideList, m_isXICMode, finished);

                                if (finished) {
                                    unregisterTask(task2Id);
                                }
                            }
                        };
                        // ask asynchronous loading of data
                        DatabaseLoadLcMSTask maptask = new DatabaseLoadLcMSTask(mapCallback);
                        maptask.initLoadAlignmentForXic(getProjectId(), m_dataset);
                        registerTask(maptask);
                    }
                } else {
                    ((XicPeptidePanel) getDataBoxPanelInterface()).dataUpdated(subTask, finished);
                }
                setLoaded(loadingId);

                if (finished) {
                    unregisterTask(taskId);
                    propagateDataChanged(ExtendedTableModelInterface.class);
                }
            }
        };

        // ask asynchronous loading of data
        m_masterQuantPeptideList = new ArrayList();
        DatabaseLoadXicMasterQuantTask task = new DatabaseLoadXicMasterQuantTask(callback);
        if (allPeptides) {
            task.initLoadPeptides(getProjectId(), m_dataset, m_masterQuantPeptideList, isXICMode());
        } else if (pepInstances != null && !pepInstances.isEmpty()) {
            List<Long> parentPepInstanceIdsL = pepInstances.stream().map(pi -> pi.getId()).collect(Collectors.toList());
            task.initLoadPeptides(getProjectId(), m_dataset, parentPepInstanceIdsL.toArray(new Long[parentPepInstanceIdsL.size()]), m_masterQuantPeptideList, isXICMode());
        } else {
            task.initLoadPeptides(getProjectId(), m_dataset, m_masterQuantProteinSet, m_masterQuantPeptideList, isXICMode());
        }
        registerTask(task);

    }

    @Override
    public void setEntryData(Object data) {
        getDataBoxPanelInterface().addSingleValue(data);
        m_dataset = (DDataset) data;
        dataChanged();
    }

    @Override
    public Object getData(boolean getArray, Class parameterType) {
        if (parameterType != null) {
            if (parameterType.equals(ResultSummary.class)) {
                return m_dataset.getResultSummary();
            }
            if (parameterType.equals(DMasterQuantPeptide.class)) {
                return ((XicPeptidePanel) getDataBoxPanelInterface()).getSelectedMasterQuantPeptide();
            }
            if (parameterType.equals(DPeptideMatch.class)) {
                DMasterQuantPeptide mqp = ((XicPeptidePanel) getDataBoxPanelInterface()).getSelectedMasterQuantPeptide();
                if (mqp == null) {
                    return null;
                }
                DPeptideInstance pi = mqp.getPeptideInstance();
                if (pi == null) {
                    return null;
                }
                return pi.getBestPeptideMatch();
            }
            if (parameterType.equals(DDataset.class)) {
                return m_dataset;
            }
            if (parameterType.equals(QuantChannelInfo.class)) {
                return m_quantChannelInfo;
            }
            if (parameterType.equals(ExtendedTableModelInterface.class)) {
                return ((GlobalTabelModelProviderInterface) getDataBoxPanelInterface()).getGlobalTableModelInterface();
            }
            if (parameterType.equals(CrossSelectionInterface.class)) {
                return ((GlobalTabelModelProviderInterface) getDataBoxPanelInterface()).getCrossSelectionInterface();
            }
            if (parameterType.equals(XicMode.class)) {
                return new XicMode(isXICMode());
            }
        }
        return super.getData(getArray, parameterType);
    }

    @Override
    public Object getData(boolean getArray, Class parameterType, boolean isList) {
        if (parameterType != null && isList) {
            if (parameterType.equals(ExtendedTableModelInterface.class)) {
                return getTableModelInterfaceList();
            }
            
            if (parameterType.equals(SecondAxisTableModelInterface.class)) {
                if (m_quantChannelInfo == null || m_masterQuantProteinSet == null) {
                    return null;
                }
                XicAbundanceProteinTableModel protTableModel = new XicAbundanceProteinTableModel();
                protTableModel.setData(m_quantChannelInfo.getQuantChannels(), m_masterQuantProteinSet);
                protTableModel.setName("Protein");
                return protTableModel;
            }
        }
        return super.getData(getArray, parameterType, isList);
    }

    @Override
    public String getFullName() {
        return m_dataset.getName() + " " + getTypeName();
    }

    private List<ExtendedTableModelInterface> getTableModelInterfaceList() {
        if (m_quantChannelInfo == null) {
            if (m_previousDataBox != null) {
                m_quantChannelInfo = (QuantChannelInfo) m_previousDataBox.getData(false, QuantChannelInfo.class);

                if (m_quantChannelInfo == null) {
                    return null;
                }
            }
        }
        List<ExtendedTableModelInterface> list = new ArrayList();
        if (m_masterQuantPeptideList != null) {
            for (DMasterQuantPeptide quantPeptide : m_masterQuantPeptideList) {
                PeptideTableModel peptideTableModel = new PeptideTableModel(null);
                peptideTableModel.setData(m_quantChannelInfo.getQuantChannels(), quantPeptide, m_isXICMode);
                list.add(peptideTableModel);
            }
        }
        return list;
    }

    @Override
    public Class[] getImportantInParameterClass() {
        Class[] classList = {DMasterQuantPeptide.class, DPeptideMatch.class};
        return classList;
    }

    @Override
    public String getImportantOutParameterValue() {
        DMasterQuantPeptide mqp = (DMasterQuantPeptide) getData(false, DMasterQuantPeptide.class);
        if (mqp != null) {
            DPeptideInstance peptideInstance = mqp.getPeptideInstance();

            if (peptideInstance != null) {
                DPeptideMatch pm = peptideInstance.getBestPeptideMatch();
                if (pm != null) {
                    Peptide peptide = pm.getPeptide();
                    if (peptide != null) {
                        return peptide.getSequence();
                    }
                }
            }
        }
        return null;
    }
}
