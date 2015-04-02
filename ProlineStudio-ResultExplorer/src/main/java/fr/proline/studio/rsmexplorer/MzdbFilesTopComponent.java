package fr.proline.studio.rsmexplorer;



import fr.proline.studio.rsmexplorer.gui.MzDBFilesPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;

import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;


/**
 * Top component to dispay the mzdb Files Panel
 */
@ConvertAsProperties(dtd = "-//fr.proline.studio.rsmexplorer//MzdbFiles//EN",
autostore = false)
@TopComponent.Description(preferredID = "MzdbFilesTopComponent",
//iconBase="SET/PATH/TO/ICON/HERE", 
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "explorer", openAtStartup = true)
@ActionID(category = "Window", id = "fr.proline.studio.rsmexplorer.MzdbFilesTopComponent")
@ActionReference(path = "Menu/Window" /*
 * , position = 333
 */)
@TopComponent.OpenActionRegistration(displayName = "#CTL_MzdbFilesAction",
preferredID = "MzdbFilesTopComponent")
@Messages({
    "CTL_MzdbFilesAction=MzDB Files",
    "CTL_MzdbFilesTopComponent=MzDB Files",
    "HINT_MzdbFilesTopComponent=MzDB Files"
})
public final class MzdbFilesTopComponent extends TopComponent  {

    
    public MzdbFilesTopComponent() {
        initComponents();
        setName(Bundle.CTL_MzdbFilesTopComponent());
        setToolTipText(Bundle.HINT_MzdbFilesTopComponent());

    }
    
 
    
    private void initComponents() {
        
         // Add panel
         setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new java.awt.Insets(5, 5, 5, 5);
        
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        add(MzDBFilesPanel.getMzdbFilesPanel(), c);
    }


    @Override
    public void componentOpened() {
        
       
        
        
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