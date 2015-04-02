/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.proline.mzscope.mzml;

import fr.profi.mzdb.model.Feature;
import fr.proline.mzscope.model.Chromatogram;
import fr.proline.mzscope.model.ExtractionParams;
import fr.proline.mzscope.model.IRawFile;
import java.io.File;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author CB205360
 */
public class MzMLRawFile implements IRawFile {

   private static Logger logger = LoggerFactory.getLogger(MzMLRawFile.class);
   private File mzMLFile;
   private List<Scan> scans;

   public MzMLRawFile(File file) {
      mzMLFile = file;
      init();
   }

   private void init() {
      try {
         long start = System.currentTimeMillis();
         logger.info("Start reading mzMLRawFile "+mzMLFile.getAbsolutePath());
         scans = mzMLReader.read(mzMLFile.getAbsolutePath());
         logger.info("File mzML read in :: " + (System.currentTimeMillis() - start) + " ms");
      } catch (Exception e) {
         logger.error("cannot read file " + mzMLFile.getAbsolutePath(), e);
      }
   }

    @Override
    public String getName() {
        return mzMLFile.getName();
    }

   
    public Chromatogram getTIC() {
      throw new UnsupportedOperationException("Not supported yet.");      
   }
    
      @Override
   public Chromatogram getBPI() {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }
    
   public Chromatogram getXIC(double min, double max) {
      Chromatogram chromatogram  = XICExtractor.extract(scans, (float)min, (float)max);      
      return chromatogram;      
   }

    @Override
    public Chromatogram getXIC(double minMz, double maxMz, float minRT, float maxRT) {
        logger.warn(" Extraction using specific time windows not yet supported ! Extraction from all scans will be done ");
        return getXIC(minMz, maxMz);
    }

  
   public fr.proline.mzscope.model.Scan getScan(int scanIndex) {
      return toModelScan(scans.get(scanIndex));
   }

   public int getScanId(double retentionTime) {
      for (Scan s : scans) {
         if (Math.abs(s.getRetentionTime() - retentionTime) < 0.001) 
            return s.getIndex();
      }
      return 0;
   }

   public int getNextScanId(int scanIndex, int msLevel) {
      return (int)Math.min(scanIndex+1, scans.size());
   }

   public int getPreviousScanId(int scanIndex, int msLevel) {
      return (int)Math.max(scanIndex-1, 0);
   }

   private fr.proline.mzscope.model.Scan toModelScan(Scan mzmlScan) {
      
      double[] masses = new double[mzmlScan.getMasses().length];
      for (int k = 0; k < masses.length; k++) {
         masses[k] = (double)mzmlScan.getMasses()[k];
      }
      return new fr.proline.mzscope.model.Scan(mzmlScan.getIndex(), mzmlScan.getRetentionTime(), masses, mzmlScan.getIntensities(), 1);
   }

   @Override
   public List<Feature> extractFeatures(ExtractionType type, ExtractionParams params) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

    @Override
    public List<Float> getMsMsEvent(double minMz, double maxMz) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}