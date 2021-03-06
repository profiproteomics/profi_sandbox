package fr.proline.studio.graphics;

/**
 * Management of ticks along an axis
 * @author JM235353
 */
public class AxisTicks {

    private static int m_maxTicks;
    private double m_min;
    private double m_max;
    
    private double m_calculatedRange;
    private double m_calculatedMin;
    private double m_calculatedMax;
    private double m_tickSpacing;
    
    private int m_nbFractionalDigits;
    private int m_nbIntegerDigits;

    private boolean m_log;
    private boolean m_isInteger;
    private boolean m_isEnum;
    
    public AxisTicks(double min, double max, int maxTicks, boolean log, boolean isInteger, boolean isEnum) {
        if (maxTicks <=1) {
            m_maxTicks = 2;
        } else {
            m_maxTicks = maxTicks;
        }
        m_log = log;
        m_isInteger =  isInteger;
        m_isEnum = isEnum;
        
        if (log) {
            m_min = Math.log10(min);
            m_max = Math.log10(max);
        } else {
            m_min = min;
            m_max = max;
        }
        calculate();
    }

    public double getTickMin() {
        return m_calculatedMin;
    }
    
    public double getTickMax() {
        return m_calculatedMax;
    }
    
    public double getTickSpacing() {
        return m_tickSpacing;
    }
    
    public int getFractionalDigits() {
        return m_nbFractionalDigits;
    }
    
     public int getIntegerDigits() {
        return m_nbIntegerDigits;
    }
    
    
    
    private void calculate() {
        if (m_log) {
            m_calculatedMin = Math.floor(m_min);
            m_calculatedMax = Math.ceil(m_max);
            m_calculatedRange = m_calculatedMax-m_calculatedMin;
            m_tickSpacing = 1;
            m_nbFractionalDigits = 0;
            m_nbIntegerDigits = 1; // Exponential display : not really used
        } else if (m_isInteger) {
            m_calculatedMin = Math.floor(m_min);
            m_calculatedMax = Math.ceil(m_max);
            m_calculatedRange = m_calculatedMax-m_calculatedMin;
            m_tickSpacing = 1;
            m_nbFractionalDigits = 0;
            m_nbIntegerDigits = Math.max((int) Math.round(Math.floor(Math.log10(Math.abs(m_calculatedMax)))), (int) Math.round(Math.floor(Math.log10(Math.abs(m_calculatedMin)))));
        } else {

            m_calculatedRange = niceNum(m_max - m_min, false);
            m_tickSpacing = niceNum(m_calculatedRange / (m_maxTicks - 1), true);
            m_nbFractionalDigits = -(int) Math.round(Math.floor(Math.log10(m_calculatedRange / (m_maxTicks - 1))));
            m_calculatedMin = Math.floor(m_min / m_tickSpacing) * m_tickSpacing;
            m_calculatedMax = Math.ceil(m_max / m_tickSpacing) * m_tickSpacing;
            m_nbIntegerDigits = Math.max((int) Math.round(Math.floor(Math.log10(Math.abs(Math.ceil(m_calculatedMax))))), (int) Math.round(Math.floor(Math.log10(Math.abs(Math.ceil(m_calculatedMin))))));
        }
    }

    /**
     * Returns a "nice" number approximately equal to range Rounds the number if
     * round = true Takes the ceiling if round = false.
     *
     * @param range the data range
     * @param round whether to round the result
     * @return a "nice" number to be used for the data range
     */
    private double niceNum(double range, boolean round) {
        double exponent;
        /**
         * exponent of range
         */
        double fraction;
        /**
         * fractional part of range
         */
        double niceFraction;
        /**
         * nice, rounded fraction
         */
        exponent = Math.floor(Math.log10(range));
        fraction = range / Math.pow(10, exponent);

        if (round) {
            if (fraction < 1.5) {
                niceFraction = 1;
            } else if (fraction < 3) {
                niceFraction = 2;
            } else if (fraction < 7) {
                niceFraction = 5;
            } else {
                niceFraction = 10;
            }
        } else {
            if (fraction <= 1) {
                niceFraction = 1;
            } else if (fraction <= 2) {
                niceFraction = 2;
            } else if (fraction <= 5) {
                niceFraction = 5;
            } else {
                niceFraction = 10;
            }
        }

        return niceFraction * Math.pow(10, exponent);
    }

}
