/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.proline.studio.msfiles;

import fr.proline.studio.dpm.serverfilesystem.ServerFile;
import java.io.File;
import java.io.Serializable;

/**
 *
 * @author AK249877
 */
public class WorkingSetEntry implements Serializable {

    public enum Location {

        LOCAL, REMOTE
    }
    
    public enum Labeling {
        DISPLAY_FILENAME, DISPLAY_PATH
    }

    private String m_filename;
    private String m_path;
    private Location m_location;
    private boolean m_existing;
    private File m_file;
    private WorkingSet m_parent;
    private Labeling m_labeling;

    public WorkingSetEntry(String filename, String path, Location location, WorkingSet parent, Labeling labeling) {
        m_filename = filename;
        m_path = path;
        m_location = location;
        m_parent = parent;
        m_labeling = labeling;

        if (location == Location.LOCAL) {
            m_file = new File(m_path);
        } else {
            m_file = new ServerFile(path, path, true, 0, 0);
        }
        m_existing = (location == Location.REMOTE || m_file.exists());
    }

    public String getFilename() {
        return m_filename;
    }
    
    public void setFilename(String filename){
        m_filename = filename;
    }

    public String getPath() {
        return m_path;
    }
    
    public void setPath(String path){
        m_path = path;
    }

    public Location getLocation() {
        return m_location;
    }
    
    public void setLocation(Location location){
        m_location = location;
    }

    public boolean exists() {
        return m_existing;
    }

    public WorkingSet getParent() {
        return m_parent;
    }

    public void resetExist() {
        m_file = new File(m_path);
        m_existing = (m_location == Location.REMOTE || m_file.exists());
    }

    public File getFile() {
        return m_file;
    }

    @Override
    public String toString() {
        if(m_labeling == WorkingSetEntry.Labeling.DISPLAY_FILENAME){
            return m_filename;
        }else{
            return m_path;
        }
    }
    

}
