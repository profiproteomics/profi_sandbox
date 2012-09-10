/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.proline.studio.rsmexplorer;

import fr.proline.core.om.model.msi.ProteinSet;
import fr.proline.core.om.model.msi.ResultSummary;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//fr.proline.studio.rsmexplorer//DataViewer//EN",
autostore = false)
@TopComponent.Description(preferredID = "DataViewerTopComponent",
//iconBase="SET/PATH/TO/ICON/HERE", 
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "fr.proline.studio.rsmexplorer.DataViewerTopComponent")
@ActionReference(path = "Menu/Window" /*
 * , position = 333
 */)
@TopComponent.OpenActionRegistration(displayName = "#CTL_DataViewerAction",
preferredID = "DataViewerTopComponent")
@Messages({
    "CTL_DataViewerAction=DataViewer",
    "CTL_DataViewerTopComponent=DataViewer Window",
    "HINT_DataViewerTopComponent=This is a DataViewer window"
})
public final class DataViewerTopComponent extends TopComponent {

    public DataViewerTopComponent() {
        initComponents();
        setName(Bundle.CTL_DataViewerTopComponent());
        setToolTipText(Bundle.HINT_DataViewerTopComponent());

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();
        proteinGroupPanel = new fr.proline.studio.rsmexplorer.gui.ProteinGroupPanel();

        tabbedPane.addTab(org.openide.util.NbBundle.getMessage(DataViewerTopComponent.class, "DataViewerTopComponent.proteinGroupPanel.TabConstraints.tabTitle_1"), proteinGroupPanel); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 867, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 815, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private fr.proline.studio.rsmexplorer.gui.ProteinGroupPanel proteinGroupPanel;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
    
    
    public void setSelectedResultSummary(ResultSummary rsm) {
        // Retrieve Protein Groups ( <=> Protein Sets )
        ProteinSet[] proteinSets = rsm.proteinSets();
        
        tabbedPane.setSelectedIndex(0); //JPM.TODO : remove 0 and put reference
        proteinGroupPanel.getProteinGroupTablePanel().setData(proteinSets);
        
    }
}
