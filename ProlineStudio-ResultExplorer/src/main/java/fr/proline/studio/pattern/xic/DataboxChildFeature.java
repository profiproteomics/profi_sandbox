package fr.proline.studio.pattern.xic;

import fr.proline.core.orm.lcms.Feature;
import fr.proline.core.orm.lcms.Peakel;
import fr.proline.core.orm.lcms.Peakel.Peak;
import fr.proline.core.orm.msi.MasterQuantPeptideIon;

import fr.proline.studio.comparedata.CompareDataInterface;
import fr.proline.studio.comparedata.CompareDataProviderInterface;
import fr.proline.studio.dam.tasks.AbstractDatabaseCallback;
import fr.proline.studio.dam.tasks.SubTask;
import fr.proline.studio.dam.tasks.xic.DatabaseLoadLcMSTask;
import fr.proline.studio.graphics.CrossSelectionInterface;
import fr.proline.studio.pattern.AbstractDataBox;
import fr.proline.studio.pattern.GroupParameter;
import fr.proline.studio.rsmexplorer.gui.xic.QuantChannelInfo;
import fr.proline.studio.rsmexplorer.gui.xic.XicFeaturePanel;
import static fr.proline.studio.rsmexplorer.gui.xic.XicFeaturePanel.VIEW_ALL_GRAPH_PEAKS;
import static fr.proline.studio.rsmexplorer.gui.xic.XicFeaturePanel.VIEW_ALL_ISOTOPES_FOR_FEATURE;
import static fr.proline.studio.rsmexplorer.gui.xic.XicFeaturePanel.VIEW_ALL_PEAKS_FOR_MAP;
import fr.proline.studio.rsmexplorer.gui.xic.XicPeakPanel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JM235353
 */
public class DataboxChildFeature extends AbstractDataBox {

    private MasterQuantPeptideIon m_masterQuantPeptideIon;
    private List<Feature> m_childFeatureList;
    private QuantChannelInfo m_quantChannelInfo;
    private List<Boolean> m_featureHasPeak;

    private List<List<Peakel>> m_peakelList;
    private List<List<List<Peak>>> m_peakList;

    public DataboxChildFeature() {
        super(DataboxType.DataboxXicChildFeature);

        // Name of this databox
        m_name = "XIC Features";
        m_description = "All  Features for a XIC Peptide Ion";

        // Register Possible in parameters
        // One Map 
        GroupParameter inParameter = new GroupParameter();
        inParameter.addParameter(MasterQuantPeptideIon.class, false);
        registerInParameter(inParameter);

        // Register possible out parameters
        GroupParameter outParameter = new GroupParameter();
        outParameter.addParameter(Feature.class, false);
        registerOutParameter(outParameter);

        outParameter = new GroupParameter();
        outParameter.addParameter(CompareDataInterface.class, true);
        registerOutParameter(outParameter);

        outParameter = new GroupParameter();
        outParameter.addParameter(CrossSelectionInterface.class, true);
        registerOutParameter(outParameter);

    }

    @Override
    public void createPanel() {
        XicFeaturePanel p = new XicFeaturePanel(true);
        p.setName(m_name);
        p.setDataBox(this);
        m_panel = p;
    }

    @Override
    public void dataChanged() {
        MasterQuantPeptideIon oldIon = m_masterQuantPeptideIon;
        m_masterQuantPeptideIon = (MasterQuantPeptideIon) m_previousDataBox.getData(false, MasterQuantPeptideIon.class);
        m_quantChannelInfo = (QuantChannelInfo) m_previousDataBox.getData(false, QuantChannelInfo.class);

        if (m_masterQuantPeptideIon != null && (oldIon != null && m_masterQuantPeptideIon.equals(oldIon))) {
            return;
        }
        if (m_masterQuantPeptideIon == null && oldIon == null) {
            return;
        }

        final int loadingId = setLoading();

        AbstractDatabaseCallback callback = new AbstractDatabaseCallback() {

            @Override
            public boolean mustBeCalledInAWT() {
                return true;
            }

            @Override
            public void run(boolean success, long taskId, SubTask subTask, boolean finished) {

                m_peakList = new ArrayList();
                if (m_childFeatureList != null) {
                    for (int i = 0; i < m_childFeatureList.size(); i++) {
                        boolean hasPeak = false;
                        List<List<Peak>> list = new ArrayList();
                        if (m_peakelList.size() >= i + 1) {
                            for (Peakel peakel : m_peakelList.get(i)) {
                                List<Peak> listPeak = peakel.getPeakList();
                                if (listPeak.size() > 0) {
                                    hasPeak = true;
                                }
                                list.add(listPeak);
                            }
                        }
                        m_peakList.add(list);
                        m_featureHasPeak.add(hasPeak);
                    }
                }

                if (subTask == null) {
                    ((XicFeaturePanel) m_panel).setData(taskId, m_childFeatureList, m_quantChannelInfo, m_featureHasPeak, finished);
                } else {
                    ((XicFeaturePanel) m_panel).dataUpdated(subTask, finished);
                }

                setLoaded(loadingId);

                if (finished) {
                    unregisterTask(taskId);
                    propagateDataChanged(CompareDataInterface.class);
                }

            }
        };

        // ask asynchronous loading of data
        m_childFeatureList = new ArrayList();
        m_featureHasPeak = new ArrayList();
        m_peakelList = new ArrayList();
        m_peakList = new ArrayList();
        
        DatabaseLoadLcMSTask task = new DatabaseLoadLcMSTask(callback);
        task.initLoadChildFeatureForPeptideIonWithPeakel(getProjectId(), m_masterQuantPeptideIon, m_childFeatureList, m_peakelList);

        registerTask(task);

    }

    private List<XicPeakPanel> getPeakTableModelList() {
        List<XicPeakPanel> list = new ArrayList();
        int viewType = ((XicFeaturePanel) m_panel).getGraphViewType();
        if (m_childFeatureList != null) {
            switch (viewType) {
                case VIEW_ALL_GRAPH_PEAKS: {
                    for (int i = 0; i < m_childFeatureList.size(); i++) {
                        Feature feature = m_childFeatureList.get(i);
                        Color color = m_quantChannelInfo.getMapColor(feature.getMap().getId());
                        String title = m_quantChannelInfo.getMapTitle(feature.getMap().getId());
                        if (m_peakelList != null && m_peakelList.size() > i) {
                            List<Peakel> peakels = m_peakelList.get(i);
                            if (peakels.size() > 0) {
                                // get the first isotope
                                int idP = 0;
                                for (Peakel peakel : peakels) {
                                    if (peakel.getIsotopeIndex() == 0) {
                                        if (m_peakList.size() > i && !m_peakList.get(i).isEmpty()) {
                                            List<Peak> peaks = m_peakList.get(i).get(idP);
                                            XicPeakPanel peakPanel = new XicPeakPanel();
                                            peakPanel.setData((long) -1, feature, peakel, peakel.getIsotopeIndex(), peaks, color, title, true);
                                            list.add(peakPanel);
                                        }
                                    }
                                    idP++;
                                }

                            }
                        }
                    }
                     break;
                }
                case VIEW_ALL_ISOTOPES_FOR_FEATURE: {
                    Feature selectedFeature = ((XicFeaturePanel) m_panel).getSelectedFeature();
                    if (selectedFeature != null) {
                        int id = m_childFeatureList.indexOf(selectedFeature);
                        if (id != -1) {
                            Color color = m_quantChannelInfo.getMapColor(selectedFeature.getMap().getId());
                            String title = m_quantChannelInfo.getMapTitle(selectedFeature.getMap().getId());
                            if (m_peakelList != null && m_peakelList.size() > id) {
                                List<Peakel> peakels = m_peakelList.get(id);
                                int nbPeakel = peakels.size();
                                if (m_peakList.size() > id && !m_peakList.get(id).isEmpty()) {
                                    List<List<Peak>> listPeaks = m_peakList.get(id);
                                    for (int p = 0; p < nbPeakel; p++) {
                                        List<Peak> peaks = listPeaks.get(p);
                                        XicPeakPanel peakPanel = new XicPeakPanel();
                                        peakPanel.setData((long) -1, selectedFeature, peakels.get(p), peakels.get(p).getIsotopeIndex(), peaks, color, title, true);
                                        list.add(peakPanel);
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
                case VIEW_ALL_PEAKS_FOR_MAP: {
                    Feature selectedFeature = ((XicFeaturePanel) m_panel).getSelectedFeature();
                    if (selectedFeature != null) {
                        int id = m_childFeatureList.indexOf(selectedFeature);
                        if (id != -1) {
                            for (int i = 0; i < m_childFeatureList.size(); i++) {
                                Feature feature = m_childFeatureList.get(i);
                                Color color = m_quantChannelInfo.getMapColor(feature.getMap().getId());
                                String title = m_quantChannelInfo.getMapTitle(feature.getMap().getId());
                                if (feature.getMap().getId() == selectedFeature.getMap().getId()) {
                                    if (m_peakelList != null && m_peakelList.size() > i) {
                                        List<Peakel> peakels = m_peakelList.get(i);
                                        if (peakels.size() > 0) {
                                            // get the first isotope
                                            int idP = 0;
                                            for (Peakel peakel : peakels) {
                                                if (peakel.getIsotopeIndex() == 0) {
                                                    if (m_peakList.size() > i && !m_peakList.get(i).isEmpty()) {
                                                        List<Peak> peaks = m_peakList.get(i).get(idP);
                                                        XicPeakPanel peakPanel = new XicPeakPanel();
                                                        peakPanel.setData((long) -1, feature, peakel, peakel.getIsotopeIndex(), peaks, color, title, true);
                                                        list.add(peakPanel);
                                                    }
                                                }
                                                idP++;
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                     break;
                }
            }

        }

        return list;
    }

    @Override
    public void setEntryData(Object data) {
        m_masterQuantPeptideIon = (MasterQuantPeptideIon) data;
        dataChanged();
    }

    @Override
    public Object getData(boolean getArray, Class parameterType) {
        if (parameterType != null) {
            if (parameterType.equals(Feature.class)) {
                return ((XicFeaturePanel) m_panel).getSelectedFeature();
            }
            if (parameterType.equals(CompareDataInterface.class)) {
                return ((CompareDataProviderInterface) m_panel).getCompareDataInterface();
            }
            if (parameterType.equals(CrossSelectionInterface.class)) {
                ((CompareDataProviderInterface) m_panel).getCrossSelectionInterface();
            }
        }
        return super.getData(getArray, parameterType);
    }

    @Override
    public Object getData(boolean getArray, Class parameterType, boolean isList) {
        if (parameterType != null && isList) {
            if (parameterType.equals(CompareDataInterface.class)) {
                return getCompareDataInterfaceList();
            }
            if (parameterType.equals(CrossSelectionInterface.class)) {
                return getCrossSelectionInterfaceList();
            }
        }
        return super.getData(getArray, parameterType, isList);
    }

    @Override
    public String getFullName() {
        return m_masterQuantPeptideIon.getCharge() + " " + getName();
    }

    private List<CompareDataInterface> getCompareDataInterfaceList() {
        List<CompareDataInterface> listCDI = new ArrayList();
        List<XicPeakPanel> listPeakPanel = getPeakTableModelList();
        for (XicPeakPanel peakPanel : listPeakPanel) {
            listCDI.add(peakPanel.getCompareDataInterface());
        }
        return listCDI;
    }

    private List<CrossSelectionInterface> getCrossSelectionInterfaceList() {
        List<CrossSelectionInterface> listCSI = new ArrayList();
        List<XicPeakPanel> listPeakPanel = getPeakTableModelList();
        for (XicPeakPanel peakPanel : listPeakPanel) {
            listCSI.add(peakPanel.getCrossSelectionInterface());
        }
        return listCSI;
    }
}