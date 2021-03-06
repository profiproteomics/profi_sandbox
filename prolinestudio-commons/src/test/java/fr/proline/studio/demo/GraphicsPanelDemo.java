package fr.proline.studio.demo;

import fr.proline.studio.graphics.BaseGraphicsPanel;
import fr.proline.studio.sampledata.Sample;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import org.openide.util.Exceptions;

/**
 *
 * @author CB205360
 */
public class GraphicsPanelDemo extends JFrame {

   private BaseGraphicsPanel graphicsPanel;

   public GraphicsPanelDemo() {
      super("Graphics Panel demo");
      graphicsPanel = new BaseGraphicsPanel(true);
      Sample sample = new Sample(3000);
      graphicsPanel.setData(sample, null);
      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(graphicsPanel, BorderLayout.CENTER);
      pack();
      setSize(450, 350);
      setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

   }

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
               GraphicsPanelDemo plot = new GraphicsPanelDemo();
               plot.setVisible(true);
            }
         });
      } catch (ClassNotFoundException ex) {
         Exceptions.printStackTrace(ex);
      } catch (InstantiationException ex) {
         Exceptions.printStackTrace(ex);
      } catch (IllegalAccessException ex) {
         Exceptions.printStackTrace(ex);
      } catch (UnsupportedLookAndFeelException ex) {
         Exceptions.printStackTrace(ex);
      }
   }

}
