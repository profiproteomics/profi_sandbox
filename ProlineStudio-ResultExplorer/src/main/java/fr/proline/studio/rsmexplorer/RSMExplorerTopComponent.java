/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.proline.studio.rsmexplorer;



import fr.proline.studio.dpm.ServerConnectionManager;
import fr.proline.studio.rsmexplorer.gui.dialog.ServerConnectionDialog;
import fr.proline.studio.rsmexplorer.node.RSMTree;
import javax.swing.SwingUtilities;

import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;

import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.WindowManager;


/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//fr.proline.studio.rsmexplorer//RSMExplorer//EN",
autostore = false)
@TopComponent.Description(preferredID = "RSMExplorerTopComponent",
//iconBase="SET/PATH/TO/ICON/HERE", 
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "explorer", openAtStartup = true)
@ActionID(category = "Window", id = "fr.proline.studio.rsmexplorer.RSMExplorerTopComponent")
@ActionReference(path = "Menu/Window" /*
 * , position = 333
 */)
@TopComponent.OpenActionRegistration(displayName = "#CTL_RSMExplorerAction",
preferredID = "RSMExplorerTopComponent")
@Messages({
    "CTL_RSMExplorerAction=Identifications",
    "CTL_RSMExplorerTopComponent=Identifications",
    "HINT_RSMExplorerTopComponent=Identifications of your Project"
})
public final class RSMExplorerTopComponent extends TopComponent  {

    
    public RSMExplorerTopComponent() {
        initComponents();
        setName(Bundle.CTL_RSMExplorerTopComponent());
        setToolTipText(Bundle.HINT_RSMExplorerTopComponent());

    }
    
 
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        explorerScrollPane = new javax.swing.JScrollPane();
        tree = RSMTree.getTree();;
        searchPanel = new fr.proline.studio.gui.SearchPanel();

        explorerScrollPane.setViewportView(tree);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(explorerScrollPane)
            .addComponent(searchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(searchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(explorerScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane explorerScrollPane;
    private fr.proline.studio.gui.SearchPanel searchPanel;
    private javax.swing.JTree tree;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        
        Thread t = new Thread() {
            @Override
            public void run() {

                ServerConnectionManager serciceConnectionMgr = ServerConnectionManager.getServerConnectionManager();
                while (serciceConnectionMgr.isConnectionAsked()) {
                    // wait for the connection to have succedeed or failed
                    try {
                        Thread.sleep(100); // JPM.TODO : one day remove the polling and write blocking code instead
                    } catch (InterruptedException ex) {
                    }
                }

                if ((serciceConnectionMgr.isNotConnected()) || (serciceConnectionMgr.isConnectionFailed())) {
                    // the user need to enter connection parameters

                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            ServerConnectionDialog serverConnectionDialog = ServerConnectionDialog.getDialog(WindowManager.getDefault().getMainWindow());
                            serverConnectionDialog.centerToScreen();
                            //databaseConnectionDialog.centerToFrame(WindowManager.getDefault().getMainWindow()); // does not work : main window has not its size most of the time at this point
                            serverConnectionDialog.setVisible(true);
                            
                            ServerConnectionManager serciceConnectionMgr = ServerConnectionManager.getServerConnectionManager();
                            if (serciceConnectionMgr.isConnectionDone()) {
                                RSMTree.getTree().startLoading();
                            }
                        }
                    });

                } else if (serciceConnectionMgr.isConnectionDone()) {
                    RSMTree.getTree().startLoading();
                }
   
            }
        };
        t.start();
        
        
        // check if the connection to the UDS is done
        
        
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

    
        
    
    
    
    
}
