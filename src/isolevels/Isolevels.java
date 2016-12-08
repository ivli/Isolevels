package isolevels;


import java.awt.Color;
import java.awt.FileDialog;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import com.ivli.roim.view.VOITransform;
import com.ivli.roim.view.WindowTarget;
import com.ivli.roim.controls.LUTControl;
import com.ivli.roim.core.Curve;
import com.ivli.roim.core.Window;
import static isolevels.Kernels.LOG55;

public class Isolevels extends javax.swing.JFrame implements WindowTarget {
    String srcName;    
    BufferedImage image = null;
    Isolevel iso = null;
    Contour zc = null;
    Shape shape = null;
    Rectangle2D rect = null;    
    Point2D cross;
    VOITransform lut;
    LUTControl lc;
    
      
    public Isolevels(final String aF) {
        initComponents();
        if (null != aF)
            setFile(aF);        
        
        lut = new VOITransform();
        lc = LUTControl.create(this);
    }    
    
    void setFile(String aName) {  
        image = null;
        iso = null;
        zc = null;
        shape = null;
        rect = null;     
        
        try {            
            image = ImageIO.read(new File(srcName=aName));                                               
            cross = new Moments(image, null).getCoG();
            jPanel.removeAll();
                
            jPanel.add(new JComponent() {
                Point p1;   
                AffineTransform scale; 
                {
                addMouseListener(new MouseAdapter() {                   
                    @Override
                    public void mousePressed(MouseEvent e) {
                        p1 = e.getPoint();
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        Point p2 = e.getPoint();             
                        if (p1.equals(p2))
                            return;
                        
                        rect = new Rectangle2D.Double(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.abs((double)p1.x - (double)p2.x), Math.abs((double)p1.y - (double)p2.y));
                       
                        AffineTransform at = AffineTransform.getScaleInstance((double)image.getWidth()/(double)getWidth(), (double)image.getHeight()/(double)getHeight()); 
                                                                            
                        Rectangle r2 = new Rectangle();
                        r2.setFrameFromDiagonal(at.transform(p1, null), at.transform(p2, null));
                      
                        iso = Isolevel.create(jRadioButton1.isSelected(), image, r2);                       
                                                    
                        Moments mom = new Moments(image, r2);
                        
                        cross.setLocation(mom.getCoG());//.setLocation(mom.XM, mom.YM);                        
                        
                        jLevel.addChangeListener((ChangeEvent evt) -> {
                            JSlider source = (JSlider)evt.getSource();
                           // if (!source. getValueIsAdjusting()) {
                                iso.update(source.getValue());
                                shape = AffineTransform.getTranslateInstance(rect.getX(), rect.getY())
                                                       .createTransformedShape(scale.createTransformedShape(iso));                                       
                                repaint();                                                    
                           // }                                                                            
                        });
                                                
                        jLevel.setMinimum((int)mom.getMin());
                        jLevel.setMaximum((int)mom.getMax());
                        jLevel.setValue((int)mom.getMed());                       
                    }});

                addMouseMotionListener(new MouseAdapter() {
                    public void mouseDragged(MouseEvent e) {                                             
                        Point p2 = e.getPoint();
                        rect = new Rectangle2D.Double(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.abs((double)p1.x - (double)p2.x), Math.abs((double)p1.y - (double)p2.y));                 
                        repaint();
                    }                   
                });
                }
                
                @Override
                public void paintComponent(Graphics g) {   
                    scale = AffineTransform.getScaleInstance((double)getWidth()/(double)image.getWidth(), (double)getHeight()/(double)image.getHeight());                   
                    BufferedImage buf = new AffineTransformOp(scale, AffineTransformOp.TYPE_NEAREST_NEIGHBOR).filter(image, null);
                    g.drawImage(buf, 0, 0, buf.getWidth(), buf.getHeight(), null);
                    
                    Graphics2D g2d = (Graphics2D)g;
                    g2d.setPaint(Color.white);
                    
                    if (null != rect)
                        g2d.draw(rect);
                    
                    ((Graphics2D)g).setPaint(Color.red);
                    
                    if (null != shape)
                        g2d.draw(shape);
                    
                    if (null != cross) {
                        Path2D p = new Path2D.Double();
                        final double x = cross.getX() * (double)getWidth()/(double)image.getWidth();
                        final double y = cross.getY() * (double)getHeight()/(double)image.getHeight();
                        p.moveTo(x - 5, y);
                        p.lineTo(x + 5, y);
                        p.moveTo(x, y - 5);
                        p.lineTo(x, y + 5);
                        g2d.setPaint(Color.BLUE);
                        g2d.draw(p);   
                    }                    
                }                  
            });    
             
        } catch (IOException ex) {            
            JOptionPane.showMessageDialog(this, "Unable to open file" + ex.getLocalizedMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }  
    }
  
    
   
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel = new javax.swing.JPanel();
        jLevel = new javax.swing.JSlider();
        jPanel1 = new javax.swing.JPanel();
        jRadioButton4 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jLevel1 = new javax.swing.JSlider();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemOpenFile = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Antenna");

        jPanel.setMinimumSize(new java.awt.Dimension(200, 200));
        jPanel.setPreferredSize(new java.awt.Dimension(400, 400));
        jPanel.setLayout(new java.awt.BorderLayout());

        jLevel.setOrientation(javax.swing.JSlider.VERTICAL);
        jLevel.setToolTipText("Adjust level");
        jLevel.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jLevelStateChanged(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        buttonGroup2.add(jRadioButton4);
        jRadioButton4.setText("LoG");
        jRadioButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton4ActionPerformed(evt);
            }
        });

        buttonGroup2.add(jRadioButton3);
        jRadioButton3.setSelected(true);
        jRadioButton3.setText("None");
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton3ActionPerformed(evt);
            }
        });

        buttonGroup2.add(jRadioButton5);
        jRadioButton5.setText("Otsu");
        jRadioButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton5ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jRadioButton3)
                    .add(jRadioButton4)
                    .add(jRadioButton5))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jRadioButton3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jRadioButton4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jRadioButton5)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Contouring");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Isolevels");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jRadioButton2)
                    .add(jRadioButton1))
                .addContainerGap(8, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jRadioButton1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jRadioButton2)
                .addContainerGap())
        );

        jLevel1.setOrientation(javax.swing.JSlider.VERTICAL);

        jMenu1.setText("File");

        jMenuItemOpenFile.setText("Open file");
        jMenuItemOpenFile.setActionCommand("OpenFile");
        jMenuItemOpenFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOpenFileActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemOpenFile);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLevel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jLevel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(7, 7, 7)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLevel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 235, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLevel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 235, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(20, 20, 20)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public double getMin() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getMax() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setWindow(Window window) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Window getWindow() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setInverted(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isInverted() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLinear(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isLinear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLUT(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BufferedImage transform(BufferedImage bi, BufferedImage bi1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Curve getWindowCurve() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    enum OP{
        NONE,
        LOG,
        OTSU
    };        
    
    void preprocess(OP aOp) {        
        switch(aOp) {
            case NONE:                   
                try {
                    image = ImageIO.read(new File(srcName));               
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Unable to open file" + ex.getLocalizedMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                } break;           
            case LOG: {                     
                BufferedImage out = new ConvolveOp(LOG55).filter(image, null);
                image = out;
                } break; 
            case OTSU: { 
                OtsuThresholder ot = new OtsuThresholder();
                int thresh = ot.compute(image.getRaster().getPixels(0, 0, image.getWidth(), image.getHeight(), (int[])null));
                final int[] ZEROS = {0,0,0};
                final int[] ONES = {255,255,255};
                
                for (int i = 0; i<image.getWidth(); ++i)
                    for (int j = 0; j < image.getHeight(); ++j) {
                        int[] pix = image.getRaster().getPixel(i, j, (int[])null);                         
                        image.getRaster().setPixel(i, j, pix[0] < thresh ? ZEROS : ONES);           
                    }
               // JOptionPane.showMessageDialog(this, "Not implemented yet", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
          
        cross = new Moments(image, null).getCoG();
        
    }
    
    private void jMenuItemOpenFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenFileActionPerformed
        FileDialog fc = new FileDialog(this);
        
        fc.setVisible(true);
        
        if (null != fc.getFile()) {
           is.setFile(fc.getDirectory() + '/' + fc.getFile());
           jPanel.validate();
           repaint();
        }
    }//GEN-LAST:event_jMenuItemOpenFileActionPerformed
    
    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        // TODO add your handling code here:
        OP op = OP.NONE;
        ///if (jRadioButton3)
        if (jRadioButton4.isSelected())
            op = OP.LOG;
        else if (jRadioButton5.isSelected())
            op = OP.LOG;
                
        preprocess(op);
        repaint();
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void jRadioButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton4ActionPerformed
        // TODO add your handling code here:
        preprocess(OP.LOG);
        repaint();
    }//GEN-LAST:event_jRadioButton4ActionPerformed

    private void jRadioButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton5ActionPerformed
        // TODO add your handling code here:
        preprocess(OP.OTSU);
        repaint();
    }//GEN-LAST:event_jRadioButton5ActionPerformed

    private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed
        // TODO add your handling code here:
        preprocess(OP.NONE);
        repaint();
    }//GEN-LAST:event_jRadioButton3ActionPerformed

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        // TODO add your handling code here:
        jRadioButton1ActionPerformed(evt);
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jLevelStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jLevelStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_jLevelStateChanged
    
    static Isolevels is;
    
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
                is = new Isolevels(args[0]);
                is.setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JSlider jLevel;
    private javax.swing.JSlider jLevel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemOpenFile;
    private javax.swing.JPanel jPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    // End of variables declaration//GEN-END:variables
    
}
