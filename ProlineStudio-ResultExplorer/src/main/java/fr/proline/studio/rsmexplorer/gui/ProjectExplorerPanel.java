package fr.proline.studio.rsmexplorer.gui;

import fr.proline.core.orm.uds.Project;
import fr.proline.core.orm.uds.UserAccount;
import fr.proline.studio.dam.AccessDatabaseThread;
import fr.proline.studio.dam.UDSDataManager;
import fr.proline.studio.dam.data.AbstractData;
import fr.proline.studio.dam.data.ProjectData;
import fr.proline.studio.dam.tasks.AbstractDatabaseCallback;
import fr.proline.studio.dam.tasks.DatabaseProjectTask;
import fr.proline.studio.dam.tasks.SubTask;
import fr.proline.studio.dpm.AccessServiceThread;
import fr.proline.studio.dpm.task.AbstractServiceCallback;
import fr.proline.studio.dpm.task.CreateProjectTask;
import fr.proline.studio.gui.DefaultDialog;
import fr.proline.studio.rsmexplorer.PropertiesTopComponent;
import fr.proline.studio.rsmexplorer.actions.ConnectAction;
import fr.proline.studio.rsmexplorer.gui.dialog.AddProjectDialog;
import fr.proline.studio.rsmexplorer.node.RSMTree;
import fr.proline.studio.utils.IconManager;
import fr.proline.studio.utils.PropertiesProviderInterface;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;
import org.slf4j.LoggerFactory;

/**
 *
 * @author JM235353
 */
public class ProjectExplorerPanel extends JPanel {

    private static ProjectExplorerPanel m_singleton = null;
    private JButton m_addProjectButton;
    private JButton m_editProjectButton;
    private JButton m_propertiesProjectButton;
    private JComboBox<ProjectItem> m_projectsComboBox = null;
    private JScrollPane m_identificationTreeScrollPane = null;
    private JScrollPane m_quantificationTreeScrollPane = null;

    public static ProjectExplorerPanel getProjectExplorerPanel() {
        if (m_singleton == null) {
            m_singleton = new ProjectExplorerPanel();
        }
        return m_singleton;
    }

    private ProjectExplorerPanel() {

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new java.awt.Insets(5, 5, 5, 5);

        // ---- Create Objects
        m_projectsComboBox = new JComboBox<>();
        m_projectsComboBox.setRenderer(new ProjectComboboxRenderer());

        JPanel buttonsPanel = createButtonPanel();

        m_identificationTreeScrollPane = new JScrollPane();
        m_identificationTreeScrollPane.getViewport().setBackground(Color.white);

        m_quantificationTreeScrollPane = new JScrollPane();
        m_quantificationTreeScrollPane.getViewport().setBackground(Color.white);
        

        // ---- Add Objects to panel
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        add(m_projectsComboBox, c);

        c.gridx++;
        c.weightx = 0;
        add(buttonsPanel, c);


        c.gridy++;
        c.gridx = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 2;
        add(m_identificationTreeScrollPane, c);

        c.gridy++;
        add(m_quantificationTreeScrollPane, c);

    }

    public void clearAll() {

        ConnectAction.setConnectionType(true, true);

        m_projectsComboBox.removeAllItems();
        
        m_identificationTreeScrollPane.setViewportView(null);
        
        m_quantificationTreeScrollPane.setViewportView(null);
    }

    private JPanel createButtonPanel() {
        JPanel buttonsPanel = new JPanel();

        buttonsPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new java.awt.Insets(5, 1, 5, 1);

        m_propertiesProjectButton = new JButton(IconManager.getIcon(IconManager.IconType.PROPERTY_SMALL_10X10));
        m_propertiesProjectButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        m_propertiesProjectButton.setToolTipText("Display Project Properties");
        m_propertiesProjectButton.setEnabled(false);

        m_editProjectButton = new JButton(IconManager.getIcon(IconManager.IconType.EDIT_SMALL_10X10));
        m_editProjectButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        m_editProjectButton.setToolTipText("Edit Project Name and Description");
        m_editProjectButton.setEnabled(false);


        m_addProjectButton = new JButton(IconManager.getIcon(IconManager.IconType.PLUS_SMALL_10X10));
        m_addProjectButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        m_addProjectButton.setToolTipText("Create a New Project");
        m_addProjectButton.setEnabled(false);

        c.gridx = 0;
        c.gridy = 0;

        buttonsPanel.add(m_propertiesProjectButton, c);

        c.gridx++;
        buttonsPanel.add(m_editProjectButton, c);

        c.gridx++;
        buttonsPanel.add(m_addProjectButton, c);

        // Interractions
        m_addProjectButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                AddProjectDialog dialog = AddProjectDialog.getAddProjectDialog(WindowManager.getDefault().getMainWindow());
                int x = (int) m_addProjectButton.getLocationOnScreen().getX() + m_addProjectButton.getWidth();
                int y = (int) m_addProjectButton.getLocationOnScreen().getY() + m_addProjectButton.getHeight();
                dialog.setLocation(x, y);
                dialog.setVisible(true);

                if (dialog.getButtonClicked() == DefaultDialog.BUTTON_OK) {

                    // data needed to create the project
                    String projectName = dialog.getProjectName();
                    String projectDescription = dialog.getProjectDescription();
                    UserAccount owner = UDSDataManager.getUDSDataManager().getProjectUser();

                    // look where to put the node (alphabetical order)
                    int insertionIndex = 1;
                    ComboBoxModel<ProjectItem> model = m_projectsComboBox.getModel();
                    int nbChildren = model.getSize();
                    for (int i = 1; i < nbChildren; i++) {
                        ProjectItem item = model.getElementAt(i);

                        String itemProjectName = item.toString();
                        if (projectName.compareToIgnoreCase(itemProjectName) >= 0) {
                            insertionIndex = i + 1;
                        } else {
                            break;
                        }

                    }

                    // Create a temporary node in the Project List
                    ProjectData projectData = new ProjectData(projectName);
                    final ProjectItem projectItem = new ProjectItem(projectData);
                    projectItem.setIsChanging(true);

                    m_projectsComboBox.insertItemAt(projectItem, insertionIndex);
                    m_projectsComboBox.setSelectedItem(projectItem);


                    AbstractServiceCallback callback = new AbstractServiceCallback() {

                        @Override
                        public boolean mustBeCalledInAWT() {
                            return true;
                        }

                        @Override
                        public void run(boolean success) {
                            if (success) {
                                projectItem.setIsChanging(false);
                                getProjectExplorerPanel().selectProject(projectItem);
                                m_projectsComboBox.repaint();
                            } else {
                                //JPM.TODO : manage error with errorMessage
                                m_projectsComboBox.removeItem(projectItem);
                            }
                        }
                    };


                    CreateProjectTask task = new CreateProjectTask(callback, projectName, projectDescription, owner.getId(), projectData);
                    AccessServiceThread.getAccessServiceThread().addTask(task);

                }
            }
        });

        m_editProjectButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                final ProjectItem projectItem = (ProjectItem) m_projectsComboBox.getSelectedItem();
                ProjectData projectData = projectItem.getProjectData();
                final Project project = projectData.getProject();

                AddProjectDialog dialog = AddProjectDialog.getModifyProjectDialog(WindowManager.getDefault().getMainWindow(), project);
                int x = (int) m_addProjectButton.getLocationOnScreen().getX() + m_addProjectButton.getWidth();
                int y = (int) m_addProjectButton.getLocationOnScreen().getY() + m_addProjectButton.getHeight();
                dialog.setLocation(x, y);
                dialog.setVisible(true);

                if (dialog.getButtonClicked() == DefaultDialog.BUTTON_OK) {

                    // data needed to create the project
                    final String projectName = dialog.getProjectName();
                    final String projectDescription = dialog.getProjectDescription();

                    if ((projectName.compareTo(project.getName()) != 0) || (projectName.compareTo(project.getName()) != 0)) {
                        projectItem.setIsChanging(true);
                        project.setName(projectName + "...");
                        m_projectsComboBox.repaint();


                        AbstractDatabaseCallback callback = new AbstractDatabaseCallback() {

                            @Override
                            public boolean mustBeCalledInAWT() {
                                return true;
                            }

                            @Override
                            public void run(boolean success, long taskId, SubTask subTask, boolean finished) {
                                projectItem.setIsChanging(false);
                                project.setName(projectName);
                                project.setDescription(projectDescription);
                                m_projectsComboBox.repaint();
                            }
                        };


                        // ask asynchronous loading of data
                        DatabaseProjectTask task = new DatabaseProjectTask(callback);
                        task.initChangeNameAndDescriptionProject(project.getId(), projectName, projectDescription);
                        AccessDatabaseThread.getAccessDatabaseThread().addTask(task);
                    }
                }
            }
        });

        m_propertiesProjectButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                ProjectItem projectItem = (ProjectItem) m_projectsComboBox.getSelectedItem();
                ProjectData projectData = projectItem.getProjectData();
                String projectName = projectData.getName();

                String dialogName = "Properties : " + projectName;


                final PropertiesTopComponent win = new PropertiesTopComponent(dialogName);
                ProjectItem[] projectItemArray = new ProjectItem[1];
                projectItemArray[0] = projectItem;
                win.setProperties(projectItemArray);
                win.open();
                win.requestActive();


            }
        });



        return buttonsPanel;
    }

    public void startLoadingProjects() {

        ConnectAction.setConnectionType(true, false);

        // Null Item corresponds to Loading Projects...
        m_projectsComboBox.addItem(null);

        final ArrayList<AbstractData> projectList = new ArrayList<>();

        // Callback used only for the synchronization with the AccessDatabaseThread
        AbstractDatabaseCallback callback = new AbstractDatabaseCallback() {

            @Override
            public boolean mustBeCalledInAWT() {
                return true;
            }

            @Override
            public void run(boolean success, long taskId, SubTask subTask, boolean finished) {

                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {

                        m_projectsComboBox.removeAllItems();

                        int nbProjects = projectList.size();
                        if (nbProjects > 0) {
                            m_projectsComboBox.addItem(new ProjectItem(null)); // Null Project corresponds to Select a Project Item
                        }
                        for (int i = 0; i < nbProjects; i++) {
                            m_projectsComboBox.addItem(new ProjectItem((ProjectData) projectList.get(i)));
                        }

                        m_addProjectButton.setEnabled(true);


                        m_projectsComboBox.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                ProjectItem item = (ProjectItem) m_projectsComboBox.getSelectedItem();
                                getProjectExplorerPanel().selectProject(item);

                                if ((item != null) && (item.getProjectData() != null) && (!item.isChanging())) {
                                    m_editProjectButton.setEnabled(true);
                                    m_propertiesProjectButton.setEnabled(true);

                                    Preferences preferences = NbPreferences.root();
                                    preferences.put("DefaultSelectedProject", item.getProjectData().getName());
                                } else {
                                    m_editProjectButton.setEnabled(false);
                                    m_propertiesProjectButton.setEnabled(false);
                                }


                            }
                        });

                        Preferences preferences = NbPreferences.root();
                        String defaultProjectName = preferences.get("DefaultSelectedProject", null);
                        if (defaultProjectName != null) {
                            int count = m_projectsComboBox.getItemCount();
                            for (int i = 0; i < count; i++) {
                                ProjectItem item = m_projectsComboBox.getItemAt(i);
                                if ((item != null) && (item.toString().compareTo(defaultProjectName) == 0)) {
                                    m_projectsComboBox.setSelectedItem(item);
                                }
                            }
                        }

                        ConnectAction.setConnectionType(false, true);
                    }
                });

            }
        };


        DatabaseProjectTask task = new DatabaseProjectTask(callback);
        task.initLoadProject(UDSDataManager.getUDSDataManager().getProjectUserName(), projectList);
        AccessDatabaseThread.getAccessDatabaseThread().addTask(task);
    }

    public void selectProject(ProjectItem projectItem) {

        if (projectItem == null) {
            m_identificationTreeScrollPane.setViewportView(null);
            m_quantificationTreeScrollPane.setViewportView(null);
            return;
        }

        ProjectData projectData = projectItem.getProjectData();

        if ((!projectItem.isChanging()) && (projectData != null)) {
            RSMTree identificationTree = RSMTree.getTree(projectData);

            m_identificationTreeScrollPane.setViewportView(identificationTree);
            //JPM.TODO

        } else {
            m_identificationTreeScrollPane.setViewportView(null);
            m_quantificationTreeScrollPane.setViewportView(null);
        }
    }

    public class ProjectComboboxRenderer extends BasicComboBoxRenderer {

        public ProjectComboboxRenderer() {
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            ProjectItem projectItem = (ProjectItem) value;

            if ((index == -1) && (projectItem == null)) {
                return this;
            }

            if (projectItem == null) {
                l.setIcon(IconManager.getIcon(IconManager.IconType.HOUR_GLASS));
                l.setText("Loading Projects...");
            } else {
                if (projectItem.getProjectData() == null) {
                    l.setIcon(null);
                } else {
                    if (projectItem.isChanging()) {
                        l.setIcon(IconManager.getIconWithHourGlass(IconManager.IconType.PROJECT));
                    } else {
                        l.setIcon(IconManager.getIcon(IconManager.IconType.PROJECT));
                    }
                }
            }
            return this;
        }
    }

    public static class ProjectItem implements PropertiesProviderInterface {

        private ProjectData m_projectData;
        private boolean m_isChanging = false;

        public ProjectItem(ProjectData projectData) {
            m_projectData = projectData;
        }

        public ProjectData getProjectData() {
            return m_projectData;
        }

        public void setIsChanging(boolean v) {
            m_isChanging = v;
        }

        public boolean isChanging() {
            return m_isChanging;
        }

        @Override
        public String toString() {
            if (m_projectData == null) {
                return "< Select a Project >";
            }
            return m_projectData.getName();
        }

        @Override
        public Sheet createSheet() {
            Project p = m_projectData.getProject();

            Sheet sheet = Sheet.createDefault();

            try {

                Sheet.Set propGroup = Sheet.createPropertiesSet();


                Node.Property prop = new PropertySupport.Reflection<>(p, Long.class, "getId", null);
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
                LoggerFactory.getLogger("ProlineStudio.ResultExplorer").error(getClass().getSimpleName() + " properties error ", e);
            }

            return sheet;
        }

        @Override
        public void loadDataForProperties(Runnable callback) {
            // nothing to do
        }
    }
}
