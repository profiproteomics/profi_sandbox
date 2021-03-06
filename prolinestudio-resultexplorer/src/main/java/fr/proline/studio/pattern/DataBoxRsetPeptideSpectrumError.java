package fr.proline.studio.pattern;

import fr.proline.core.orm.msi.dto.DPeptideMatch;
import fr.proline.studio.rsmexplorer.gui.spectrum.RsetPeptideSpectrumErrorPanel;
import fr.proline.studio.rsmexplorer.gui.spectrum.PeptideFragmentationData;

/**
 * Databox for a Spectrum
 *
 * @author JM235353
 */
public class DataBoxRsetPeptideSpectrumError extends AbstractDataBox {

    private DPeptideMatch m_previousPeptideMatch = null;

    public DataBoxRsetPeptideSpectrumError() {
        super(DataboxType.DataBoxRsetPeptideSpectrumError, DataboxStyle.STYLE_RSET);

        // Name of this databox
        m_typeName = "Spectrum Error";
        m_description = "Spectrum Error of a Peptide";

        // Register in parameters
        GroupParameter inParameter = new GroupParameter();
        inParameter.addParameter(DPeptideMatch.class, false);
        inParameter.addParameter(PeptideFragmentationData.class, false);
        registerInParameter(inParameter);

        // Register possible out parameters
        // none
    }

    @Override
    public void createPanel() {
        RsetPeptideSpectrumErrorPanel p = new RsetPeptideSpectrumErrorPanel();
        p.setName(m_typeName);
        p.setDataBox(this);
        setDataBoxPanelInterface(p);
    }

    @Override
    public void dataChanged() {

        final PeptideFragmentationData fragmentationData = (PeptideFragmentationData) m_previousDataBox.getData(false, PeptideFragmentationData.class);
        final DPeptideMatch peptideMatch = (fragmentationData != null) ? fragmentationData.getPeptideMatch() : null;

        if ((m_previousPeptideMatch == peptideMatch) && (fragmentationData == null)) {
            return;
        }
        m_previousPeptideMatch = peptideMatch;

        if (peptideMatch == null) {
            ((RsetPeptideSpectrumErrorPanel) getDataBoxPanelInterface()).setData(null, null);
        } else {
            ((RsetPeptideSpectrumErrorPanel) getDataBoxPanelInterface()).setData(peptideMatch, fragmentationData);
        }
    }
}
