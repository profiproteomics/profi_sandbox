/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.proline.studio.rsmexplorer.gui.dialog.xic;

import fr.proline.studio.parameter.BooleanParameter;
import fr.proline.studio.parameter.DoubleParameter;
import fr.proline.studio.parameter.ObjectParameter;
import fr.proline.studio.parameter.ParameterList;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.*;
import org.openide.util.NbPreferences;

/**
 * panel with the different parameters for computing quantitation profile
 * @author MB243701
 */
public class QuantProfileXICPanel extends JPanel {
    
    private ParameterList m_parameterList;
    
    private JScrollPane m_scrollPane;    
    private JTabbedPane m_tabbedPane;
    
    private JCheckBox m_useOnlySpecificPeptidesChB;
    private JCheckBox m_discardMissedCleavedPeptidesChB;
    private JCheckBox m_discardOxidizedPeptidesChB;
    
    private JCheckBox m_applyProfileClusteringChB;
    
    private JComboBox<String> m_abundanceSummarizerMethodCB;
    
    private final static String[] ABUNDANCE_SUMMARIZER_METHOD_VALUES = {"Mean", "Mean of top 3 peptides", "Median", "Median Profile", "Normalized Median Profile", "Sum"};
    private final static String[] ABUNDANCE_SUMMARIZER_METHOD_KEYS = {"MEAN", "MEAN_OF_TOP3", "MEDIAN","MEDIAN_PROFILE", "NORMALIZED_MEDIAN_PROFILE","SUM"};
     
    private JTextField m_peptideStatTestsAlpha;
    private JCheckBox m_applyPepNormalizationChB;
    private JCheckBox m_applyPepMissValInferenceChB;
    private JCheckBox m_applyPepVarianceCorrectionChB;
    private JCheckBox m_applyPepTTestChB;
    private JCheckBox m_applyPepZTestChB;
    
    private JTextField m_proteinStatTestsAlpha;
    private JCheckBox m_applyProtNormalizationChB;
    private JCheckBox m_applyProtMissValInferenceChB;
    private JCheckBox m_applyProtVarianceCorrectionChB;
    private JCheckBox m_applyProtTTestChB;
    private JCheckBox m_applyProtZTestChB;
    
    
    
    public QuantProfileXICPanel() {
        super();
        init();
    }
    
    private void init() {
        m_parameterList = new ParameterList("XicProfileParameters");
        createParameters();
        m_parameterList.updateIsUsed(NbPreferences.root());
        initPanel();
        restoreParameters(NbPreferences.root());
    }
    
    
    public final void createParameters() {
        m_peptideStatTestsAlpha = new JTextField();
        DoubleParameter m_peptideStatTestsAlphaParameter = new DoubleParameter("peptideStatTestsAlpha", "Peptide Stat Tests Alpha", m_peptideStatTestsAlpha, new Double(0.01), null, null);
        m_parameterList.add(m_peptideStatTestsAlphaParameter);
       
        m_proteinStatTestsAlpha = new JTextField();
        DoubleParameter m_proteinStatTestsAlphaParameter = new DoubleParameter("proteinStatTestsAlpha", "Protein Stat Tests Alpha", m_proteinStatTestsAlpha, new Double(0.01), null, null);
        m_parameterList.add(m_proteinStatTestsAlphaParameter);
        
        m_discardMissedCleavedPeptidesChB = new JCheckBox("Discard Missed Cleaved Peptides");
        BooleanParameter discardMissedCleavedPeptidesParameter = new BooleanParameter("discardMissedCleavedPeptides", "Discard Missed Cleaved Peptides", m_discardMissedCleavedPeptidesChB, true);
        m_parameterList.add(discardMissedCleavedPeptidesParameter);
        
        m_discardOxidizedPeptidesChB = new JCheckBox("Discard Oxidized Peptides");
        BooleanParameter discardOxidizedPeptidesParameter = new BooleanParameter("discardOxidizedPeptides", "Discard Oxidized Peptides", m_discardOxidizedPeptidesChB, true);
        m_parameterList.add(discardOxidizedPeptidesParameter);
        
        m_applyPepNormalizationChB = new JCheckBox("Apply Normalization");
        BooleanParameter applyPepNormalizationParameter = new BooleanParameter("applyPepNormalization", "Apply Normalization on peptides", m_applyPepNormalizationChB, true);
        m_parameterList.add(applyPepNormalizationParameter);

        m_applyProtNormalizationChB = new JCheckBox("Apply Normalization");
        BooleanParameter applyProtNormalizationParameter = new BooleanParameter("applyProtNormalization", "Apply Normalization on proteins", m_applyProtNormalizationChB, true);
        m_parameterList.add(applyProtNormalizationParameter);

        m_applyPepMissValInferenceChB = new JCheckBox("Apply Missing Value Inference");
        BooleanParameter applyPepMissValInferenceParameter = new BooleanParameter("applyPepMissValInference", "Apply Miss Val Inference on peptides", m_applyPepMissValInferenceChB, true);
        m_parameterList.add(applyPepMissValInferenceParameter);

        m_applyProtMissValInferenceChB = new JCheckBox("Apply Missing Value Inference");
        BooleanParameter applyProtMissValInferenceParameter = new BooleanParameter("applyProtMissValInference", "Apply Miss Val Inference on proteins", m_applyProtMissValInferenceChB, true);
        m_parameterList.add(applyProtMissValInferenceParameter);
        
        m_applyPepVarianceCorrectionChB = new JCheckBox("Apply Variance Correction");
        BooleanParameter applyPepVarianceCorrectionParameter = new BooleanParameter("applyPepVarianceCorrection", "Apply Variance Correction on peptides", m_applyPepVarianceCorrectionChB, true);
        m_parameterList.add(applyPepVarianceCorrectionParameter);
        
        m_applyProtVarianceCorrectionChB = new JCheckBox("Apply Variance Correction");
        BooleanParameter applyProtVarianceCorrectionParameter = new BooleanParameter("applyProtVarianceCorrection", "Apply Variance Correction on proteins", m_applyProtVarianceCorrectionChB, true);
        m_parameterList.add(applyProtVarianceCorrectionParameter);
        
        m_applyPepTTestChB = new JCheckBox("Apply T-Test (peptide)");
        BooleanParameter applyPepTTestParameter = new BooleanParameter("applyPepTTest", "Apply TTest on peptides", m_applyPepTTestChB, true);
        m_parameterList.add(applyPepTTestParameter);
        
        m_applyProtTTestChB = new JCheckBox("Apply T-Test (protein)");
        BooleanParameter applyProtTTestParameter = new BooleanParameter("applyProtTTest", "Apply TTest on proteins", m_applyProtTTestChB, true);
        m_parameterList.add(applyProtTTestParameter);

        m_applyPepZTestChB = new JCheckBox("Apply Z-Test (peptide)");
        BooleanParameter applyPepZTestParameter = new BooleanParameter("applyPepZTest", "Apply ZTest on peptides", m_applyPepZTestChB, true);
        m_parameterList.add(applyPepZTestParameter);
        
        m_applyProtZTestChB = new JCheckBox("Apply Z-Test (protein)");
        BooleanParameter applyProtZTestParameter = new BooleanParameter("applyProtZTest", "Apply ZTest on proteins", m_applyProtZTestChB, true);
        m_parameterList.add(applyProtZTestParameter);

        m_applyProfileClusteringChB = new JCheckBox("Apply Profile Clustering");
        BooleanParameter applyProfileClusteringParameter = new BooleanParameter("applyProfileClustering", "Apply Profile Clustering", m_applyProfileClusteringChB, true);
        m_parameterList.add(applyProfileClusteringParameter);
        
        m_useOnlySpecificPeptidesChB = new JCheckBox("Use Only Specific Peptides");
        BooleanParameter useOnlySpecificPeptidesParameter = new BooleanParameter("useOnlySpecificPeptides", "Use Only Specific Peptides", m_useOnlySpecificPeptidesChB, true);
        m_parameterList.add(useOnlySpecificPeptidesParameter);
        
        
        m_abundanceSummarizerMethodCB = new JComboBox(ABUNDANCE_SUMMARIZER_METHOD_VALUES);
        ObjectParameter<String> m_abundanceSummarizerMethodParameter = new ObjectParameter<>("abundanceSummarizerMethod", "Abundance Summarizer Method", m_abundanceSummarizerMethodCB, ABUNDANCE_SUMMARIZER_METHOD_VALUES, ABUNDANCE_SUMMARIZER_METHOD_KEYS,  0, null);
        m_parameterList.add(m_abundanceSummarizerMethodParameter);
    }
    
    private void initPanel() {
        this.setLayout(new BorderLayout());
      
        m_tabbedPane = new JTabbedPane();
        m_tabbedPane.addTab("Pep. selection", null, getPeptidesSelectionPanel(), "Specify peptides to consider for quantitation");
        m_tabbedPane.addTab("Pep. configuration", null, getQuantPeptidesConfigurationPanel(), "Parameters used for peptides quantitation");
        m_tabbedPane.addTab("Prot. configuration", null, getQuantProteinsConfigurationPanel(), "Parameters used for proteins quantitation");

        m_scrollPane = new JScrollPane();
        m_scrollPane.setViewportView(m_tabbedPane);
        m_scrollPane.createVerticalScrollBar();
        add(m_scrollPane, BorderLayout.CENTER);
    }
    
    
    private JPanel getPeptidesSelectionPanel(){
        JPanel northPanel = new JPanel(new BorderLayout());
        JPanel pepSelectionPanel = new JPanel();
        pepSelectionPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);

        // useOnlySpecificPeptides
        c.gridx = 0;
        c.gridy=0;
        c.weightx = 1;
//        c.gridwidth = 2;
        pepSelectionPanel.add(m_useOnlySpecificPeptidesChB, c);
        
        // discardMissedCleavedPeptides
        c.gridy++;
        c.gridx = 0;
        c.weightx = 1;
//        c.gridwidth = 1;
        pepSelectionPanel.add(m_discardMissedCleavedPeptidesChB, c);
        
        // discardOxidizedPeptides
        c.gridy++;
//        c.gridx++;
        c.weightx = 1;
//        c.gridwidth = 1;
        pepSelectionPanel.add(m_discardOxidizedPeptidesChB, c);
        
        northPanel.add(pepSelectionPanel, BorderLayout.NORTH);
        return northPanel;        
    }
    
     private JPanel getQuantPeptidesConfigurationPanel(){
        JPanel northPanel = new JPanel(new BorderLayout());
        JPanel pepQuantConfigPanel = new JPanel();
        pepQuantConfigPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);

     
        // applyNormalization
        c.gridx = 0;
        c.gridy=0;
        c.weightx = 1;
        c.gridwidth = 2;
        pepQuantConfigPanel.add(m_applyPepNormalizationChB, c);
                        
        // applyMissValInference
        c.gridy++;
        pepQuantConfigPanel.add(m_applyPepMissValInferenceChB, c);
        
        // peptideStatTestsAlpha
        c.gridy++;
        c.weightx = 0;
        c.gridwidth = 1;
        pepQuantConfigPanel.add(new JLabel("Peptide Stat Tests Alpha:"), c);
        
        c.gridx++;  
        c.weightx = 1;
        c.gridwidth = 1;
        pepQuantConfigPanel.add(m_peptideStatTestsAlpha, c);
        
        // applyTTest
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy++;
        c.gridx = 0;
        pepQuantConfigPanel.add(m_applyPepTTestChB, c);
        m_applyPepTTestChB.addActionListener(new ActionListener() {
         @Override
            public void actionPerformed(ActionEvent e) {
                m_applyPepVarianceCorrectionChB.setEnabled(m_applyPepTTestChB.isSelected());
                if(!m_applyPepTTestChB.isSelected())
                    m_applyPepVarianceCorrectionChB.setSelected(false);                    
                }
            });
        
        // applyVarianceCorrection
        c.gridy++;
        m_applyPepVarianceCorrectionChB.setEnabled(m_applyPepTTestChB.isSelected());
        if(!m_applyPepTTestChB.isSelected())
            m_applyPepVarianceCorrectionChB.setSelected(false);                    
        
        pepQuantConfigPanel.add(m_applyPepVarianceCorrectionChB, c);
        
        // applyZTest
        c.gridy++;
        pepQuantConfigPanel.add(m_applyPepZTestChB, c);
        northPanel.add(pepQuantConfigPanel, BorderLayout.NORTH);
        return northPanel;        
    }
     
      private JPanel getQuantProteinsConfigurationPanel(){
        JPanel northPanel = new JPanel(new BorderLayout());
        JPanel protQuantConfigPanel = new JPanel();
        protQuantConfigPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);
        
        c.gridx = 0;
        c.gridy=0;
        c.weightx = 0;
        c.gridwidth = 1;
        JLabel abundanceSummarizerMethodLabel = new JLabel("Abundance Summarizer Method :");
        abundanceSummarizerMethodLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        protQuantConfigPanel.add(abundanceSummarizerMethodLabel, c);
        
        c.gridx++;
        c.gridwidth = 1;
        c.weightx = 1;
        protQuantConfigPanel.add(m_abundanceSummarizerMethodCB, c);       
        
        // applyProfileClustering
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy++;
        protQuantConfigPanel.add(m_applyProfileClusteringChB, c);

        
        // applyNormalization
        c.gridy++;
        protQuantConfigPanel.add(m_applyProtNormalizationChB, c);
        
                
        // applyMissValInference
        c.gridy++;
        protQuantConfigPanel.add(m_applyProtMissValInferenceChB, c);
        
        
        // proteinStatTestsAlpha
        c.gridy++;
        c.gridx = 0;
        c.weightx = 0;
        protQuantConfigPanel.add(new JLabel("Protein Stat Tests Alpha:"), c);
        
        c.gridx++;  
        c.weightx = 1;
        protQuantConfigPanel.add(m_proteinStatTestsAlpha, c);
        
        
        // applyTTest
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy++;
        c.gridx = 0;
        protQuantConfigPanel.add(m_applyProtTTestChB, c);
        m_applyProtTTestChB.addActionListener(new ActionListener() {
         @Override
            public void actionPerformed(ActionEvent e) {
                m_applyProtVarianceCorrectionChB.setEnabled(m_applyProtTTestChB.isSelected());
            }
        });
                
        // applyVarianceCorrection
        c.gridy++;
        m_applyProtVarianceCorrectionChB.setEnabled(m_applyProtTTestChB.isSelected());
        protQuantConfigPanel.add(m_applyProtVarianceCorrectionChB, c);
        
        // applyZTest
        //c.gridy++;
        //c.gridx = 0;
        c.gridy++;
        protQuantConfigPanel.add(m_applyProtZTestChB, c);

        northPanel.add(protQuantConfigPanel, BorderLayout.NORTH);
        return northPanel;        
        
    }
      
    public ParameterList getParameterList() {
        return m_parameterList;
    }
    
    public Map<String,Object> getQuantParams(){
        Map<String,Object> params = new HashMap<>();
        
        params.put("use_only_specific_peptides", m_useOnlySpecificPeptidesChB.isSelected());
        params.put("discard_missed_cleaved_peptides", m_discardMissedCleavedPeptidesChB.isSelected());
        params.put("discard_oxidized_peptides", m_discardOxidizedPeptidesChB.isSelected());
        params.put("apply_profile_clustering", m_applyProfileClusteringChB.isSelected());
        params.put("abundance_summarizer_method", ABUNDANCE_SUMMARIZER_METHOD_KEYS[m_abundanceSummarizerMethodCB.getSelectedIndex()]);
          
        Map<String,Object> peptideStatConfigMap = new HashMap<>();
        peptideStatConfigMap.put("stat_tests_alpha", m_peptideStatTestsAlpha.getText());
        peptideStatConfigMap.put("apply_normalization", m_applyPepNormalizationChB.isSelected());
        peptideStatConfigMap.put("apply_miss_val_inference", m_applyPepMissValInferenceChB.isSelected());
        peptideStatConfigMap.put("apply_variance_correction", m_applyPepVarianceCorrectionChB.isSelected());
        peptideStatConfigMap.put("apply_ttest", m_applyPepTTestChB.isSelected());
        peptideStatConfigMap.put("apply_ztest", m_applyPepZTestChB.isSelected());
        params.put("peptide_stat_config",peptideStatConfigMap);

        Map<String,Object> proteinStatConfigMap = new HashMap<>();
        proteinStatConfigMap.put("stat_tests_alpha", m_proteinStatTestsAlpha.getText());
        proteinStatConfigMap.put("apply_normalization", m_applyProtNormalizationChB.isSelected());
        proteinStatConfigMap.put("apply_miss_val_inference", m_applyProtMissValInferenceChB.isSelected());
        proteinStatConfigMap.put("apply_variance_correction", m_applyProtVarianceCorrectionChB.isSelected());
        proteinStatConfigMap.put("apply_ttest", m_applyProtTTestChB.isSelected());
        proteinStatConfigMap.put("apply_ztest", m_applyProtZTestChB.isSelected());
        params.put("protein_stat_config",proteinStatConfigMap);
        
       return params;
    }
        
    
    private void restoreParameters(Preferences preferences) {

        Float peptideStatTestsAlpha = preferences.getFloat("peptideStatTestsAlpha", 0.01f );
        m_peptideStatTestsAlpha.setText(""+peptideStatTestsAlpha);
        
        Float proteinStatTestsAlpha = preferences.getFloat("proteinStatTestsAlpha", 0.01f );
        m_proteinStatTestsAlpha.setText(""+proteinStatTestsAlpha);
        
        Boolean discardMissedCleavedPeptides = preferences.getBoolean("discardMissedCleavedPeptides", true );
        m_discardMissedCleavedPeptidesChB.setSelected(discardMissedCleavedPeptides);
        
        Boolean discardOxidizedPeptides = preferences.getBoolean("discardOxidizedPeptides", true );
        m_discardOxidizedPeptidesChB.setSelected(discardOxidizedPeptides);
        
        Boolean applyPepNormalization = preferences.getBoolean("applyPepNormalization", true );
        m_applyPepNormalizationChB.setSelected(applyPepNormalization);
        
        Boolean applyPepMissValInference = preferences.getBoolean("applyPepMissValInference", true );
        m_applyPepMissValInferenceChB.setSelected(applyPepMissValInference);
        
        Boolean applyPepVarianceCorrection = preferences.getBoolean("applyPepVarianceCorrection", true );
        m_applyPepVarianceCorrectionChB.setSelected(applyPepVarianceCorrection);
        
        Boolean applyPepTTest = preferences.getBoolean("applyPepTTest", true );
        m_applyPepTTestChB.setSelected(applyPepTTest);
        
        Boolean applyPepZTest = preferences.getBoolean("applyPepZTest", true );
        m_applyPepZTestChB.setSelected(applyPepZTest);

        Boolean applyProtNormalization = preferences.getBoolean("applyProtNormalization", true );
        m_applyProtNormalizationChB.setSelected(applyProtNormalization);
        
        Boolean applyProtMissValInference = preferences.getBoolean("applyProtMissValInference", true );
        m_applyProtMissValInferenceChB.setSelected(applyProtMissValInference);
        
        Boolean applyProtVarianceCorrection = preferences.getBoolean("applyProtVarianceCorrection", true );
        m_applyProtVarianceCorrectionChB.setSelected(applyProtVarianceCorrection);
        
        Boolean applyProtTTest = preferences.getBoolean("applyProtTTest", true );
        m_applyProtTTestChB.setSelected(applyProtTTest);
        
        Boolean applyProtZTest = preferences.getBoolean("applyProtZTest", true );
        m_applyProtZTestChB.setSelected(applyProtZTest);
        
        Boolean applyProfileClustering = preferences.getBoolean("applyProfileClustering", true );
        m_applyProfileClusteringChB.setSelected(applyProfileClustering);
        
        Boolean useOnlySpecificPeptides = preferences.getBoolean("useOnlySpecificPeptides", true );
        m_useOnlySpecificPeptidesChB.setSelected(useOnlySpecificPeptides);
               
        String abundanceSummarizerMethod = preferences.get("abundanceSummarizerMethod", ABUNDANCE_SUMMARIZER_METHOD_VALUES[0] );
        m_abundanceSummarizerMethodCB.setSelectedItem(abundanceSummarizerMethod);
    }
    

}
