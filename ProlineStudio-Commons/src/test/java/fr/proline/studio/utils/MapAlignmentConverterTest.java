package fr.proline.studio.utils;

import fr.proline.core.orm.lcms.MapAlignment;
import fr.proline.core.orm.lcms.MapAlignmentPK;
import fr.proline.core.orm.lcms.MapSet;
import fr.proline.core.orm.lcms.MapTime;
import fr.proline.core.orm.lcms.ProcessedMap;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.util.Exceptions;

/**
 *
 * @author MB243701
 */
public class MapAlignmentConverterTest {
    private Double elutionTime = 407.94376;
    private Long sourceMapId = (long)29;
    private Long targetMapId = (long)27;
    private Long alnRefMapId = (long)29;
    private List<MapAlignment> listMapAlignment;
    private MapAlignment m2927 ;
    private double[] time = {407.94376, 659.6494, 705.3053, 944.1511, 1052.0923, 1220.0706, 1457.3612, 1624.9949, 1657.2917, 1864.2368, 1942.5608, 2096.854, 2293.2031, 2535.8982, 2635.487, 2906.7554, 2980.6082, 3137.6746, 3264.684, 3367.2642, 3659.351, 3798.6387, 3956.2402, 4097.009, 4277.588, 4420.4556, 4604.7593, 4811.3354, 4874.9756, 5094.384, 5219.1846, 5289.661, 5535.9893, 5726.557, 5911.125, 6053.6196, 6231.0005, 6307.292, 6408.385, 6635.782};
    private double[] deltaTime = {3.8952484, 9.7387085, 18.569397, 19.529938, 22.678467, 29.814209, 30.39087, 31.20636, 32.172607, 31.84552, 32.23578, 34.20923, 30.90503, 34.45923, 34.312256, 32.250977, 33.95044, 32.752808, 32.096558, 33.23413, 30.826538, 29.483398, 31.168823, 30.7417, 28.568115, 30.141602, 31.343262, 29.369385, 23.242188, 14.140137, 9.879395, 8.674805, 7.6279297, 8.34082, 7.520508, 7.4555664, 6.6621094, 5.6552734, 7.0273438, 3.3122559};
    
    public MapAlignmentConverterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        listMapAlignment = new ArrayList();
        ProcessedMap refMap = new ProcessedMap();
        refMap.setId(new Long(29));
        refMap.setIsAlnReference(true);
        ProcessedMap destMap = new ProcessedMap();
        destMap.setId(new Long(27));
        destMap.setIsAlnReference(false);
        MapSet mapSet = new MapSet();
        mapSet.setId(new Long(7));
        mapSet.setAlnReferenceMap(refMap);
        m2927 = new MapAlignment();
        MapAlignmentPK pk = new MapAlignmentPK();
        pk.setFromMapId(new Long(29));
        pk.setToMapId(new Long(27));
        m2927.setId(pk);
        m2927.setMapSet(mapSet);
        m2927.setSourceMap(refMap);
        m2927.setDestinationMap(destMap);
        List<MapTime> mapTimeList = new ArrayList();
        for (int i = 0; i < time.length; i++) {
            MapTime mapTime= new MapTime(time[i], deltaTime[i]);
            mapTimeList.add(mapTime);
        }
        m2927.setMapTimeList(mapTimeList);
        listMapAlignment.add(m2927);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of convertElutionTime method, of class MapAlignmentConverter.
     * @throws java.lang.Exception
     */
    @Test
    public void testConvertElutionTime() throws Exception {
        System.out.println("convertElutionTime");
        Double expResult = 407.94376 + 3.8952484;
        Double result = MapAlignmentConverter.convertElutionTime(elutionTime, sourceMapId, targetMapId, listMapAlignment, alnRefMapId);
        assertEquals(expResult, result);
    }

    /**
     * Test of getMapAlgn method, of class MapAlignmentConverter.
     */
    @Test
    public void testGetMapAlgn() {
        System.out.println("getMapAlgn");
        MapAlignment expResult = m2927;
        MapAlignment result = MapAlignmentConverter.getMapAlgn(sourceMapId, targetMapId, listMapAlignment);
        assertEquals(expResult, result);
    }

    /**
     * Test of calcTargetMapElutionTime method, of class MapAlignmentConverter.
     */
    @Test
    public void testCalcTargetMapElutionTime() {
        System.out.println("calcTargetMapElutionTime");
        Double expResult = 407.94376 + 3.8952484;
        Double result;
        try {
            result = MapAlignmentConverter.calcTargetMapElutionTime(m2927, elutionTime);
            assertEquals(expResult, result);
            
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        
    }

    /**
     * Test of linearInterpolation method, of class MapAlignmentConverter.
     */
    @Test
    public void testLinearInterpolation() {
        try {
            System.out.println("linearInterpolation");
            Double expResult = 3.8952484;
            Double result = MapAlignmentConverter.linearInterpolation(elutionTime, time, deltaTime);
            assertEquals(expResult, result);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
}
