/*
 * @cea 
 * http://www.profiproteomics.fr
 * create date: 26 nov. 2018
 */
package fr.proline.studio.rsmexplorer.gui.ptm.mark;

import fr.proline.studio.rsmexplorer.gui.ptm.ViewContext;
import fr.proline.studio.rsmexplorer.gui.ptm.ViewPtmAbstract;
import fr.proline.studio.rsmexplorer.gui.ptm.PtmSiteAA;
import fr.proline.studio.rsmexplorer.gui.ptm.ViewSetting;
import fr.proline.studio.utils.CyclicColorPalette;

import java.awt.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Karine XUE
 */
public class PtmMarkView extends ViewPtmAbstract {

    private static Logger logger = LoggerFactory.getLogger("ProlineStudio.rsmexplorer.ptm");
    private static final BasicStroke STROKE = new BasicStroke(1.5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);

    /**
     * One letter
     */
    String m_type;
    Color m_color;
    /**
     * location in the protein, to paint above the type figure
     */
    int _locationProtein;
    int _ajustNTermAt1;//useful for N-termini at protein 1;

    public PtmMarkView(PtmSiteAA pAA) {
        this.m_color = pAA.getColor();
        this.m_type = Character.toString(pAA.getPtmTypeChar());
        this._locationProtein = pAA.getModifyLocProtein();
        if (pAA.isNTermAt1()) {
            this._ajustNTermAt1 = 1;
        } else {
            this._ajustNTermAt1 = 0;
        }

    }

    @Override
    public void setBeginPoint(int x, int y) {
        this.m_x = x;
        this.m_y = y;
    }

    /**
     * location in a protein where this amino acid of protein begin to show
     *
     * @param g
     * @param viewContext
     */
    @Override
    public void paint(Graphics2D g, ViewContext viewContext) {

        double aaWidth = ViewSetting.WIDTH_AA;

        FontMetrics fm = g.getFontMetrics(ViewSetting.FONT_PTM);

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        this.x0 = (int)(this.m_x + aaWidth+ (this._locationProtein - viewContext.getAjustedLocation() - 1 + this._ajustNTermAt1) * aaWidth);
        //this.x0 = this.m_x + (this._locationProtein - locationAjusted-1) * ViewSetting.WIDTH_AA;
        this.y0 = this.m_y + ViewSetting.HEIGHT_AA; //reserve location line

        g.setColor(m_color);
        //draw the box
        g.setStroke(STROKE);
        g.drawLine(x0, y0, (int)(x0 + aaWidth), y0);
        g.drawLine((int)(x0 + aaWidth), y0, (int)(x0 + aaWidth), y0 + ViewSetting.HEIGHT_AA);
        g.drawLine((int)(x0 + aaWidth), y0 + ViewSetting.HEIGHT_AA, x0, y0+ ViewSetting.HEIGHT_AA);
        g.drawLine(x0, y0 + ViewSetting.HEIGHT_AA, x0, y0);
        
        int yBottom = (int) (y0 + ViewSetting.HEIGHT_AA * 1.5);
        int xCenter = (int)(x0 + aaWidth / 2);
        g.drawLine(xCenter, y0 + ViewSetting.HEIGHT_AA, xCenter, yBottom);
        g.drawLine(x0, yBottom, x0 + ViewSetting.HEIGHT_AA, yBottom);

        //draw PTM Type in the box
        g.setFont(ViewSetting.FONT_PTM);
        int stringWidth = fm.stringWidth(m_type);
        // assume that letters are squared: do not use ascent or height but reuse font width to position ptm letter in the middle of the box
        g.drawString(m_type, xCenter - stringWidth / 2, y0 + ViewSetting.HEIGHT_AA - (ViewSetting.HEIGHT_AA - stringWidth)/2); //x, y are base line begin x, y


        //draw the location number upper
        g.setColor(CyclicColorPalette.GRAY_TEXT_DARK);
        g.setFont(ViewSetting.FONT_NUMBER);
        fm = g.getFontMetrics(ViewSetting.FONT_NUMBER);
        int descent = fm.getDescent();

        stringWidth = fm.stringWidth(String.valueOf(_locationProtein));
        if (stringWidth > (aaWidth - ViewSetting.BORDER_GAP + 3)) {
            Font smallerFont = ViewSetting.FONT_NUMBER_DIAGONAL;
            g.setFont(smallerFont);
            fm = g.getFontMetrics(smallerFont);
            stringWidth = fm.stringWidth(String.valueOf(_locationProtein));
        }

        g.drawString(String.valueOf(_locationProtein), xCenter - stringWidth / 2, y0 - descent / 2);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    }

    @Override
    public String toString() {
        return "ViewPtmMark{" + "m_type=" + m_type + ", _locationProtein=" + _locationProtein + ", _ajustNTermAt1=" + _ajustNTermAt1 + '}';
    }
}
