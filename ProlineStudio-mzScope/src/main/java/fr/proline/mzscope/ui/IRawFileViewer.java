package fr.proline.mzscope.ui;

import fr.proline.mzscope.model.Chromatogram;
import fr.proline.mzscope.model.IFeature;
import fr.proline.mzscope.model.IRawFile;
import fr.proline.mzscope.model.MsnExtractionRequest;
import fr.proline.mzscope.utils.MzScopeCallback;
import fr.proline.mzscope.utils.MzScopeConstants;
import java.awt.Color;

/**
 *
 * @author CB205360
 */
public interface IRawFileViewer {
   
   /**
    * Extract a Chromatogram according to the specified parameters and display the results in this panel. The specified 
    * callback is called in the AWT event dispatch thread when the display finished.
    * 
    * @param params the XIC extraction parameters 
    * @param mode the display mode to use
    * @param callback that must be called back after XIC extraction.
    */
   public void extractAndDisplayChromatogram(MsnExtractionRequest params, MzScopeConstants.DisplayMode mode, MzScopeCallback callback);
   /**
    * Display the chromatogram and return the plot color
    * 
    * @param chromato
    * @return the color used to display the sepecified c
    */ 
   public Color displayChromatogram(Chromatogram chromato, MzScopeConstants.DisplayMode mode);
   
   /**
    * Display the specified Feature in this component.
    * 
    * @param f : the feature to be displayed
    */
   public void displayFeature(IFeature f);
   
   /**
    * Display the scan specified by the index parameter.
    * 
    * @param index 
    */
   public void displayScan(long index);

   /**
    * Returns the current RawFile ie the the raw file associated with the current Chromatogram  (see below)
    * 
    * @return 
    */
   public IRawFile getCurrentRawfile();

   /**
    * Returns the active Chromatogram ie the chromatogram that will be used to select or iterate over scans.
    * 
    * @return 
    */
   public Chromatogram getCurrentChromatogram();
   
   /**
    * Returns the color associated with the specified rawFilename
    * 
    * @param rawFilename
    * @return 
    */
   public Color getPlotColor(String rawFilename);
   
   /**
    * Returns the mode used by this component to display XIC
    * 
    *  @return the mode used by this component to display XIC.
    */
   public MzScopeConstants.DisplayMode getXicModeDisplay();
}
