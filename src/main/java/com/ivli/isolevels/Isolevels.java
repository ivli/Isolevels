package com.ivli.isolevels;


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
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
///import javax.swing.GroupLayout;

import com.ivli.roim.view.VOITransform;
///import com.ivli.roim.view.WindowTarget;
import com.ivli.roim.controls.LUTControl;
import com.ivli.roim.core.Curve;
import com.ivli.roim.core.Window;
import com.ivli.roim.view.ImageView;
import com.ivli.roim.controls.CalcPanel;
import com.ivli.roim.controls.ChartView;
import com.ivli.roim.controls.FrameControl;
import com.ivli.roim.controls.LUTControl;
import com.ivli.roim.controls.ROIListPanel;
import com.ivli.roim.view.IImageView;
import com.ivli.roim.core.ImageType;
import com.ivli.roim.core.TimeSlice;
import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.ImageFactory;
import com.ivli.roim.events.*;
import com.ivli.roim.io.ImageServiceProvider;
import com.ivli.roim.view.ROIManager;
import com.ivli.roim.view.Settings;

//import com.ivli.roim.FrameChangeListener;
import com.ivli.roim.*;


import java.awt.image.Kernel;


public class Isolevels extends javax.swing.JFrame {//implements  FrameChangeListener, WindowChangeListener {//}, ZoomChangeListener, ProgressListener{ ///implements WindowTarget 
    String        srcName;    
    BufferedImage image = null;
    Isolevel      iso = null;    
    Shape         shape = null;
    Rectangle     rect = null;  //ROI in screen coordinates  
    Point2D       cross;
    Point2D       cross2;
    VOITransform   iVoi;
   /// LUTControl     iLut;   
    Moments        mom;
    

    public Isolevels() {
        initComponents();
        //if (null != aF)
         //   setFile(aF);        
        
       iVoi = new VOITransform();
       ///iLut = LUTControl.create();
    }    
    
    void setFile(String aName) {  
         
        try {            
            image = ImageIO.read(new File(srcName=aName));                                               
            mom = Moments.create(image.getWidth(), image.getHeight(), image.getRaster().getPixels(0, 0, image.getWidth(), image.getHeight(), (int[])null));
            double []d = mom.calculate(0, 0, image.getWidth(), image.getHeight()).getCoG();
            cross = new Point2D.Double(d[0], d[1]);
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
                    
                    void update() {       
                        double []d = {.0,.0};
                        shape = scale.createTransformedShape(iso.update(jLevel.getValue(), !jRadioButton1.isSelected(), d));     
                        cross2 = new Point2D.Double(d[0] , d[1] );
                        repaint();
                    }
                    
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        Point p2 = e.getPoint();             
                        if (p1.equals(p2))
                            return;
                       
                        rect = new Rectangle(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y));
                       
                        
                        System.out.printf("---------------------------------------------------------------\n" +
                                          "-> new ROI set (%d, %d):(%d, %d)\n",rect.x, rect.y, rect.width, rect.height
                                         );
                        
                        AffineTransform at = AffineTransform.getScaleInstance((double)image.getWidth()/(double)getWidth(), (double)image.getHeight()/(double)getHeight()); 
                                                                            
                        Rectangle r2 = new Rectangle();
                        r2.setFrameFromDiagonal(at.transform(p1, null), at.transform(p2, null));
                      
                        iso = Isolevel.create(image, r2);                       
                                                    
                        double [] d = mom.calculate(r2.x, r2.y, r2.width, r2.height).getCoG();
                        
                        cross.setLocation(d[0], d[1]);     
                        
                        jLevel.addChangeListener((ChangeEvent evt) -> {update();}); 
                              
                        jRadioButton1.addChangeListener((ChangeEvent evt) -> {update();});                                                     
                                                                          
                        jLevel.setMinimum((int)mom.getMin());
                        jLevel.setMaximum((int)mom.getMax());
                        jLevel.setValue((int)mom.getExpectation());                       
                    }});

                addMouseMotionListener(new MouseAdapter() {
                    public void mouseDragged(MouseEvent e) {                                             
                        Point p2 = e.getPoint();
                        rect = new Rectangle(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y));                 
                        repaint();
                    }                   
                });
                }
                
                @Override
                public void paintComponent(Graphics g) {   
                    scale = AffineTransform.getScaleInstance((double)getWidth()/(double)image.getWidth(), (double)getHeight()/(double)image.getHeight());                   
                    System.out.printf("now scale is %f, %f", scale.getScaleX(), scale.getScaleY());
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
                    
                    if (null != cross2) {
                        Path2D p = new Path2D.Double();
                        final double x = cross2.getX() * (double)getWidth()/(double)image.getWidth();
                        final double y = cross2.getY() * (double)getHeight()/(double)image.getHeight();
                        p.moveTo(x - 3, y - 3);
                        p.lineTo(x + 3, y + 3);
                        p.moveTo(x + 3, y - 3);
                        p.lineTo(x - 3, y + 3);
                        g2d.setPaint(Color.GREEN);
                        g2d.draw(p);   
                    }                    
                }                  
            });    
             
        } catch (IOException ex) {            
            JOptionPane.showMessageDialog(this, "Unable to open file" + ex.getLocalizedMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }  
    }
  
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
        jWindowWidth = new javax.swing.JSlider();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemOpenFile = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Isolevels");

        jPanel.setMinimumSize(new java.awt.Dimension(200, 200));
        jPanel.setPreferredSize(new java.awt.Dimension(400, 400));
        jPanel.setLayout(new java.awt.BorderLayout());

        jLevel.setOrientation(javax.swing.JSlider.VERTICAL);
        jLevel.setToolTipText("window level");
        jLevel.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jLevelStateChanged(evt);
            }
        });

        jWindowWidth.setOrientation(javax.swing.JSlider.VERTICAL);
        jWindowWidth.setToolTipText("window width");
        jWindowWidth.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jWindowStateChanged(evt);
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton3)
                    .addComponent(jRadioButton4)
                    .addComponent(jRadioButton5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton2)
                    .addComponent(jRadioButton1))
                .addContainerGap(8, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton2)
                .addContainerGap())
        );

        

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jWindowWidth, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jWindowWidth, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
/*
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
*/
    enum OP{
        NONE,
        LOG,
        OTSU
    };        

    static Isolevels is;
    
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
                BufferedImage out = new ConvolveOp(new Kernel(5,5,Kernels.LOG_5x5)).filter(image, null);
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

    private void jLevelStateChanged(javax.swing.event.ChangeEvent evt) {
       ///level up/down
       int val = jLevel.getValue();
       System.out.printf("jLevel.getValue() = %d", val);

    }

    private void jWindowStateChanged(javax.swing.event.ChangeEvent evt) {
        int val = jWindowWidth.getValue();
        System.out.printf("jWindowWidth.getValue() = %d", val);
        int full = jWindowWidth.getMaximum() - jWindowWidth.getMinimum();

     }
    ///static Isolevels is;
    
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
                is = new Isolevels();
                is.setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JSlider jLevel;
    private javax.swing.JSlider jWindowWidth;
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
