
package isolevels;

import java.awt.Color;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Isolevels extends javax.swing.JFrame {
    BufferedImage image = null;
    Isolevel iso = null;
    Shape shape = null;
    Rectangle2D rect = null;
   
    public Isolevels(final String aF) {
        initComponents();
        String srcName = aF;
        File srcFile = new File(srcName);
       
        try {
            image = ImageIO.read(srcFile);
                        
            jPanel.add(new JComponent() {
                Point p1;
                Point p2;

                {
                addMouseListener(new MouseAdapter() {                   
                    @Override
                    public void mousePressed(MouseEvent e) {
                        p1 = e.getPoint();
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        p2 = e.getPoint();
                        
                        rect = new Rectangle2D.Double(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.abs((double)p1.x - (double)p2.x), Math.abs((double)p1.y - (double)p2.y));
                       
                        AffineTransform at = AffineTransform.getScaleInstance((double)image.getWidth() / (double)getWidth(), (double)image.getHeight() / (double)getHeight()); 
                                                                            
                        Rectangle r2 = new Rectangle();
                        r2.setFrameFromDiagonal(at.transform(p1, null), at.transform(p2, null));
                       
                        WritableRaster wr = image.getRaster().createWritableChild(r2.x, r2.y, r2.width, r2.height, 0, 0, null); 
                        /*
                        image = new BufferedImage(image.getColorModel(), wr, false, null);                                                 
                        */
                        iso = Isolevel.create(wr, null, null);
                
                        jLevel.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                JSlider source = (JSlider)e.getSource();
                                if (!source. getValueIsAdjusting()) {
                                    iso.update(source.getValue());

                                    try {    
                                        AffineTransform to = at.createInverse();
                                        AffineTransform to2 = AffineTransform.getTranslateInstance(rect.getX(), rect.getY());                                        
                                        shape = to.createTransformedShape(iso);
                                        shape = to2.createTransformedShape(shape);                                        
                                        repaint();
                                    } catch (NoninvertibleTransformException ex) {

                                    }                    
                                }
                            }                                                
                        });
                                                
                        jLevel.setMinimum((int)iso.min);
                        jLevel.setMaximum((int)iso.max);
                        jLevel.setValue((int)((iso.max - iso.min) / 2.));                       
                    }});

                addMouseMotionListener(new MouseAdapter() {
                    public void mouseDragged(MouseEvent e) {                                             
                        p2 = e.getPoint();
                        rect = new Rectangle2D.Double(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.abs((double)p1.x - (double)p2.x), Math.abs((double)p1.y - (double)p2.y));                 
                        repaint();
                    }                   
                });
                }
                @Override
                public void paintComponent(Graphics g) {                                                          
                    AffineTransformOp op = new AffineTransformOp(
                                                 AffineTransform.getScaleInstance((double)getWidth()/(double)image.getWidth(), (double)getHeight()/(double)image.getHeight()), 
                                                    AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                    BufferedImage buf = op.filter(image, null);
                    g.drawImage(buf, 0, 0, buf.getWidth(), buf.getHeight(), null);
                    
                    ((Graphics2D)g).setPaint(Color.white);
                    
                    if (null != rect)
                        ((Graphics2D)g).draw(rect);
                    
                    ((Graphics2D)g).setPaint(Color.red);
                    
                    if (null != shape)
                        ((Graphics2D)g).draw(shape);
                    
                }  
                
            });    
             
        } catch (IOException ex) {            
            System.out.println("unable to open: " + srcName + ", cause:" + ex.getLocalizedMessage());             
            System.exit(-1);
        }  
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel = new javax.swing.JPanel();
        jLevel = new javax.swing.JSlider();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Antenna");

        jPanel.setMinimumSize(new java.awt.Dimension(200, 200));
        jPanel.setLayout(new java.awt.BorderLayout());

        jLevel.setOrientation(javax.swing.JSlider.VERTICAL);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                .add(18, 18, 18)
                .add(jLevel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 49, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLevel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            javax.swing.UIManager.LookAndFeelInfo[] installedLookAndFeels=javax.swing.UIManager.getInstalledLookAndFeels();
            for (int idx=0; idx<installedLookAndFeels.length; idx++)
                if ("Nimbus".equals(installedLookAndFeels[idx].getName())) {
                    javax.swing.UIManager.setLookAndFeel(installedLookAndFeels[idx].getClassName());
                    break;
                }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Isolevels.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Isolevels.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Isolevels.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Isolevels.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {  
                System.out.println("-->Starting up");    
                new Isolevels(args[0]).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider jLevel;
    private javax.swing.JPanel jPanel;
    // End of variables declaration//GEN-END:variables
    
}
