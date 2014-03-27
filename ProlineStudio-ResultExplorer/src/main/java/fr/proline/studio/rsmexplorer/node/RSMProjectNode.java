package fr.proline.studio.rsmexplorer.node;

import fr.proline.core.orm.uds.Project;
import fr.proline.studio.dam.AccessDatabaseThread;
import fr.proline.studio.dam.data.AbstractData;
import fr.proline.studio.dam.data.ProjectData;
import fr.proline.studio.dam.tasks.AbstractDatabaseCallback;
import fr.proline.studio.dam.tasks.DatabaseProjectTask;
import fr.proline.studio.dam.tasks.SubTask;
import fr.proline.studio.utils.IconManager;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeModel;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.nodes.Node.Property;


/**
 * Node for the opened Projects
 * @author JM235353
 */
public class RSMProjectNode extends RSMNode {

    
    public RSMProjectNode(AbstractData data) {
        super(RSMNode.NodeTypes.PROJECT, data);
    }


    public Project getProject() {
        return ((ProjectData) getData()).getProject();
    }
    
    @Override
    public ImageIcon getIcon() {
        return getIcon(IconManager.IconType.MOLECULE);
    }

    @Override
    public String toString() {
        return "Identifications";
    }
    
    public void changeNameAndDescription(final String newName, final String newDescription) {
        
        final Project project = getProject();
        String name = project.getName();
        String description = project.getDescription();
        
        if (((newName != null) && (newName.compareTo(name) != 0)) || ((newDescription != null) && (newDescription.compareTo(description) != 0))) {
            setIsChanging(true);
            project.setName(newName + "...");
            ((DefaultTreeModel) RSMTree.getCurrentTree().getModel()).nodeChanged(this);

            final RSMProjectNode projectNode = this;
            
            AbstractDatabaseCallback callback = new AbstractDatabaseCallback() {

                @Override
                public boolean mustBeCalledInAWT() {
                    return true;
                }

                @Override
                public void run(boolean success, long taskId, SubTask subTask, boolean finished) {
                    setIsChanging(false);
                    project.setName(newName);
                    project.setDescription(newDescription);
                    ((DefaultTreeModel) RSMTree.getCurrentTree().getModel()).nodeChanged(projectNode);
                }
            };


            // ask asynchronous loading of data
            DatabaseProjectTask task = new DatabaseProjectTask(callback);
            task.initChangeNameAndDescriptionProject(project.getId(), newName, newDescription);
            AccessDatabaseThread.getAccessDatabaseThread().addTask(task);
        }
    }

    @Override
    public void loadDataForProperties(Runnable callback) {
        // nothing to do
        callback.run();
    }
    
    @Override
    public Sheet createSheet() {
        Project p = getProject();
        
        Sheet sheet = Sheet.createDefault();
        
        try {

            Sheet.Set propGroup = Sheet.createPropertiesSet();
            
         
            Property prop = new PropertySupport.Reflection<>(p, Long.class, "getId", null);
            prop.setName("id");
            propGroup.put(prop);
            
            prop = new PropertySupport.Reflection<>(p, String.class, "getName", null);
            prop.setName("name");
            propGroup.put(prop);
            
            prop = new PropertySupport.Reflection<>(p, String.class, "getDescription", null);
            prop.setName("description");
            propGroup.put(prop);
            
            sheet.put(propGroup);

        } catch (NoSuchMethodException e) {
            m_logger.error(getClass().getSimpleName() + " properties error ", e);
        }
        
        return sheet;
    }
    
}
