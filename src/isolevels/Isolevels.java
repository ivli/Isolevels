package isolevels;

import java.awt.Color;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Isolevels extends javax.swing.JFrame {
    String srcName;    
    BufferedImage image = null;
    Isolevel iso = null;
    Contour zc = null;
    Shape shape = null;
    Rectangle2D rect = null;
    
    Point2D cross;// = new Path2D.Double();
   
    public Isolevels(final String aF) {
        initComponents();
        srcName = aF;
        //File srcFile = new File(srcName);
        Moments mom;
        try {
            image = ImageIO.read(new File(srcName));
            mom = new Moments(image);                                    
            cross = mom.getCoG(null);///new Point2D.Double(mom.XM, mom.YM);
            
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
                        /*
                        WritableRaster wr = image.getRaster().createWritableChild(r2.x, r2.y, r2.width, r2.height, 0, 0, null); 
                        
                        image = new BufferedImage(image.getColorModel(), wr, false, null);                                                 
                        */
                        iso = Isolevel.create(image, r2, null);
                        cross = mom.getCoG(r2);
                        AffineTransform to;
                        
                        try { 
                            to = at.createInverse();
                            cross.setLocation(mom.XM, mom.YM);                        

                            System.out.printf("--> %f, %f\n", cross.getX(), cross.getY());
                        
                        
                        jLevel.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                JSlider source = (JSlider)e.getSource();
                                if (!source. getValueIsAdjusting()) {
                                    iso.update(source.getValue());
                                    AffineTransform to2 = AffineTransform.getTranslateInstance(rect.getX(), rect.getY());                                        
                                    shape = to.createTransformedShape(iso);
                                    shape = to2.createTransformedShape(shape);                                       
                                    repaint();                                                    
                                }
                            }                                                
                        });
                                                
                        jLevel.setMinimum((int)iso.min);
                        jLevel.setMaximum((int)iso.max);
                        jLevel.setValue((int)((iso.max - iso.min) / 2.));                       
                   
                        } catch (NoninvertibleTransformException ex) {  
                            
                        }
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
            System.out.println("unable to open: " + srcName + ", cause:" + ex.getLocalizedMessage());             
            System.exit(-1);
        }  
    }
    
    
    Kernel makeKernel(float[] aK){             
        return new Kernel ((int)Math.sqrt(aK.length), (int)Math.sqrt(aK.length), aK);
    };
   
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel = new javax.swing.JPanel();
        jLevel = new javax.swing.JSlider();
        jConvolve = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Antenna");

        jPanel.setMinimumSize(new java.awt.Dimension(200, 200));
        jPanel.setPreferredSize(new java.awt.Dimension(400, 400));
        jPanel.setLayout(new java.awt.BorderLayout());

        jLevel.setOrientation(javax.swing.JSlider.VERTICAL);

        jConvolve.setToolTipText("Convolve Image");
        jConvolve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jConvolveActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(18, 18, 18)
                        .add(jLevel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(26, 26, 26)
                        .add(jConvolve)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLevel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 379, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jConvolve))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jConvolveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jConvolveActionPerformed
        
        final float[] emboss = new float[] { -2,0,0,   0,1,0,   0,0,2 };
        final float[] blurring = new float[] { 1f/9f,1f/9f,1f/9f, 1f/9f,1f/9f,1f/9f, 1f/9f,1f/9f,1f/9f };
        final float[] sharpening = new float[] { -1,-1,-1,   -1,9,-1,   -1,-1,-1 };
        
        final float[] Gauss_5x5 = new float[] { 1,  4,  7,  4, 1,
                                                4, 16, 26, 16, 4,
                                                7, 26, 41, 26, 7,
                                                4, 16, 26, 16, 4,
                                                1,  4,  7,  4, 1
        };
        
        final float[] Gauss_3x3_sigma1 = new float[] {0.077847f, 0.123317f, 0.077847f, 
                                                      0.123317f, 0.195346f, 0.123317f, 
                                                      0.077847f, 0.123317f, 0.077847f};
        
        final float[] Gauss_3x3_sigma3 = new float[] {0.107035f, 0.113092f, 0.107035f,
                                                      0.113092f, 0.119491f, 0.113092f,
                                                      0.107035f, 0.113092f, 0.107035f};
        
        final float[] Laplacian_3x3 = new float[] { 0, -1,  0,
                                                   -1,  4, -1,
                                                    0, -1,  0};    
        
        final float[] Laplacian_3x3_sigma_1point4 = new float[] { -0.0037480f,  -0.0073566f,  -0.0037480f,
                                                                  -0.0073566f,  -0.0127458f,  -0.0073566f,
                                                                  -0.0037480f,  -0.0073566f,  -0.0037480f};
        
        final float[] Laplacian_11x11_sigma_2 = new float[] { 
            8.110750e-006f, 1.962940e-005f, 3.710001e-005f, 5.598273e-005f, 6.981805e-005f, 7.471898e-005f, 6.981805e-005f, 5.598273e-005f, 3.710001e-005f, 1.962940e-005f, 8.110750e-006f, 
            1.962940e-005f, 4.397295e-005f, 7.471898e-005f, 9.853654e-005f, 1.075274e-004f, 1.083062e-004f, 1.075274e-004f, 9.853654e-005f, 7.471898e-005f, 4.397295e-005f, 1.962940e-005f, 
            3.710001e-005f, 7.471898e-005f, 1.054362e-004f, 9.849036e-005f, 5.732105e-005f, 3.247663e-005f, 5.732105e-005f, 9.849036e-005f, 1.054362e-004f, 7.471898e-005f, 3.710001e-005f, 
            5.598273e-005f, 9.853654e-005f, 9.849036e-005f, 0.000000e+000f, -1.606347e-004f, -2.426973e-004f, -1.606347e-004f, 0.000000e+000f, 9.849036e-005f, 9.853654e-005f, 5.598273e-005f, 
            6.981805e-005f, 1.075274e-004f, 5.732105e-005f, -1.606347e-004f, -4.674443e-004f, -6.179644e-004f, -4.674443e-004f, -1.606347e-004f, 5.732105e-005f, 1.075274e-004f, 6.981805e-005f, 
            7.471898e-005f, 1.083062e-004f, 3.247663e-005f, -2.426973e-004f, -6.179644e-004f, -8.002805e-004f, -6.179644e-004f, -2.426973e-004f, 3.247663e-005f, 1.083062e-004f, 7.471898e-005f, 
            6.981805e-005f, 1.075274e-004f, 5.732105e-005f, -1.606347e-004f, -4.674443e-004f, -6.179644e-004f, -4.674443e-004f, -1.606347e-004f, 5.732105e-005f, 1.075274e-004f, 6.981805e-005f, 
            5.598273e-005f, 9.853654e-005f, 9.849036e-005f, 0.000000e+000f, -1.606347e-004f, -2.426973e-004f, -1.606347e-004f, 0.000000e+000f, 9.849036e-005f, 9.853654e-005f, 5.598273e-005f, 
            3.710001e-005f, 7.471898e-005f, 1.054362e-004f, 9.849036e-005f, 5.732105e-005f, 3.247663e-005f, 5.732105e-005f, 9.849036e-005f, 1.054362e-004f, 7.471898e-005f, 3.710001e-005f, 
            1.962940e-005f, 4.397295e-005f, 7.471898e-005f, 9.853654e-005f, 1.075274e-004f, 1.083062e-004f, 1.075274e-004f, 9.853654e-005f, 7.471898e-005f, 4.397295e-005f, 1.962940e-005f, 
            8.110750e-006f, 1.962940e-005f, 3.710001e-005f, 5.598273e-005f, 6.981805e-005f, 7.471898e-005f, 6.981805e-005f, 5.598273e-005f, 3.710001e-005f, 1.962940e-005f, 8.110750e-006f, }; 

        final float[] Laplacian_7x7_sigma_1and4 = new float[] {
            2.502123e-004f, 5.777487e-004f, 8.316200e-004f, 8.967564e-004f, 8.316200e-004f, 5.777487e-004f, 2.502123e-004f, 
            5.777487e-004f, 9.295234e-004f, 5.289226e-004f, 5.056474e-005f, 5.289226e-004f, 9.295234e-004f, 5.777487e-004f, 
            8.316200e-004f, 5.289226e-004f, -2.021333e-003f, -3.967426e-003f, -2.021333e-003f, 5.289226e-004f, 8.316200e-004f, 
            8.967564e-004f, 5.056474e-005f, -3.967426e-003f, -6.873873e-003f, -3.967426e-003f, 5.056474e-005f, 8.967564e-004f, 
            8.316200e-004f, 5.289226e-004f, -2.021333e-003f, -3.967426e-003f, -2.021333e-003f, 5.289226e-004f, 8.316200e-004f, 
            5.777487e-004f, 9.295234e-004f, 5.289226e-004f, 5.056474e-005f, 5.289226e-004f, 9.295234e-004f, 5.777487e-004f, 
            2.502123e-004f, 5.777487e-004f, 8.316200e-004f, 8.967564e-004f, 8.316200e-004f, 5.777487e-004f, 2.502123e-004f}; 
          
        
        final float[] Laplacian_5x5 = new float[] {-1f, -1f, -1f, -1f, -1f, 
                                                   -1f, -1f, -1f, -1f, -1f, 
                                                   -1f, -1f, 24f, -1f, -1f, 
                                                   -1f, -1f, -1f, -1f, -1f, 
                                                   -1f, -1f, -1f, -1f, -1f};
        
        
        if (jConvolve.isSelected()) {
            ConvolveOp op = new ConvolveOp(makeKernel(Laplacian_5x5));
            BufferedImage out = op.filter(image, null);
            image = out; 
            
        } else {      
            try {
                image = ImageIO.read(new File(srcName));
               
            } catch (IOException ex) {
                System.out.print(ex);
                System.exit(-1);
            } 
        }
          
        cross = new Moments(image).getCoG(null);
        
        repaint();
    }//GEN-LAST:event_jConvolveActionPerformed
    
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
    private javax.swing.JCheckBox jConvolve;
    private javax.swing.JSlider jLevel;
    private javax.swing.JPanel jPanel;
    // End of variables declaration//GEN-END:variables
    
}
