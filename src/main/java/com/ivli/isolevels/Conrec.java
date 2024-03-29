
package com.ivli.isolevels;

/*
 * Conrec.java
 *
 * Created on 5 August 2001, 15:03
 *
 * Copyright (c) 1996-1997 Nicholas Yue
 *
 * This software is copyrighted by Nicholas Yue. This code is base on the work of
 * Paul D. Bourke CONREC.F routine
 *
 * The authors hereby grant permission to use, copy, and distribute this
 * software and its documentation for any purpose, provided that existing
 * copyright notices are retained in all copies and that this notice is included
 * verbatim in any distributions. Additionally, the authors grant permission to
 * modify this software and its documentation for any purpose, provided that
 * such modifications are not distributed without the explicit consent of the
 * authors and that existing copyright notices are retained in all copies. Some
 * of the algorithms implemented by this software are patented, observe all
 * applicable patent law.
 *
 * IN NO EVENT SHALL THE AUTHORS OR DISTRIBUTORS BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE, ITS DOCUMENTATION, OR ANY DERIVATIVES THEREOF,
 * EVEN IF THE AUTHORS HAVE BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * THE AUTHORS AND DISTRIBUTORS SPECIFICALLY DISCLAIM ANY WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, AND NON-INFRINGEMENT.  THIS SOFTWARE IS PROVIDED ON AN
 * "AS IS" BASIS, AND THE AUTHORS AND DISTRIBUTORS HAVE NO OBLIGATION TO PROVIDE
 * MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 */
 


/**
 * Conrec a straightforward method of contouring some surface represented a regular 
 * triangular mesh. 
 *
 * Ported from the C++ code by Nicholas Yue (see above copyright notice).
 * @see http://paulbourke.net/papers/conrec for full description
 * of code and original C++ source.
 *
 * @author  Bradley White
 * @version 1.0 
 */

/**
 *  modified for binary separation and work with integers  
 * 
 * @author  Igor Likhachev
 * @version  
 */
public class Conrec {

    @FunctionalInterface
    public interface Render { 
        public void addSegment(double startX, double startY, double endX, double endY); 
    }

    private double  []  h   =  new double [5];
    private int     []  sh  =  new int    [5];
    private double  []  xh  =  new double [5];
    private double  []  yh  =  new double [5];
    
    // Object that knows how to draw the contour
    private Render render = null;
 
    /** Creates new Conrec */
    public  Conrec(Render render) {
        if (render == null){
            throw new IllegalArgumentException ("Render null");
        }
        this.render = render;
    }
  
    /**
     *     contour is a contouring subroutine for rectangularly spaced data 
     *
     *     It emits calls to a line drawing subroutine supplied by the user
     *     which draws a contour map corresponding to real*4data on a evenly
     *     spaced rectangular grid.     
     *
     * @param d  - matrix of data to contour
     * @param ilb,iub,jlb,jub - initial indexes and dimensions of bounds of data matrix 
     *
     *             The following two, one dimensional arrays (x and y) contain the horizontal and
     *             vertical coordinates of each sample points.    
     * @param z  - contour level 
     * 
     */
    public void contour(int []d, int ilb, int width, int jlb, int height, int z) {
        int m1;
        int m2;
        int m3;
        //int case_value;      
        double x1 = 0.0;
        double x2 = 0.0;
        double y1 = 0.0;
        double y2 = 0.0;
        int i, j, m;
        final int iub = width - 1;
        final int jub = height - 1;
                      
        // The indexing of im and jm should be noted as it has to start from zero
        // unlike the fortran counter part
        final int []im = {0,1,1,0};
        final int []jm = {0,0,1,1};
        
        // Note that castab is arranged differently from the FORTRAN code because
        // Fortran and C/C++ arrays are transposed of each other, in this case
        // it is more tricky as castab is in 3 dimension
        final int [][][] castab=
        {
            {
                {0,0,8},{0,2,5},{7,6,9}
            },
            {
                {0,3,4},{1,3,1},{4,3,0}
            },
            {
                {9,6,7},{5,2,0},{8,0,0}
            }
        };
        
        for (j=(jub-1); j>=jlb; j--) {
            for (i=ilb; i<=iub-1; i++) {                               
                int d11 = d[i+width*j];                
                int d12 = d[i+width*(j+1)];                
                int d21 = d[(i+1)+width*j];                               
                int d22 = d[(i+1)+width*(j+1)];
               
                final int dmin = Math.min(Math.min(d11, d12), Math.min(d21, d22));               
                final int dmax = Math.max(Math.max(d11, d12), Math.max(d21, d22));
              
                if (z>=dmin && z<=dmax) {
                    for (m=4; m>=0; m--) {
                        if (m>0) {
                            // The indexing of im and jm should be noted as it has to
                            // start from zero
                            h[m]  = d[(i+im[m-1])+width*(j+jm[m-1])] - z;
                            xh[m] = i+im[m-1];
                            yh[m] = j+jm[m-1];
                        } else {
                            h[0]  = 0.25*(h[1]+h[2]+h[3]+h[4]);
                            xh[0] = 0.5*(i + i + 1);
                            yh[0] = 0.5*(j + j + 1);
                        }
                        if (h[m]>0.0) {
                            sh[m] = 1;
                        } else if (h[m]<0.0) {
                            sh[m] = -1;
                        } else
                            sh[m] = 0;
                    }
                    //
                    // Note: at this stage the relative heights of the corners and the
                    // centre are in the h array, and the corresponding coordinates are
                    // in the xh and yh arrays. The centre of the box is indexed by 0
                    // and the 4 corners by 1 to 4 as shown below.
                    // Each triangle is then indexed by the parameter m, and the 3
                    // vertices of each triangle are indexed by parameters m1,m2,and
                    // m3.
                    // It is assumed that the centre of the box is always vertex 2
                    // though this is important only when all 3 vertices lie exactly on
                    // the same contour level, in which case only the side of the box
                    // is drawn.
                    //
                    //
                    //      vertex 4 +-------------------+ vertex 3
                    //               | \               / |
                    //               |   \    m-3    /   |
                    //               |     \       /     |
                    //               |       \   /       |
                    //               |  m=2    X   m=2   |       the centre is vertex 0
                    //               |       /   \       |
                    //               |     /       \     |
                    //               |   /    m=1    \   |
                    //               | /               \ |
                    //      vertex 1 +-------------------+ vertex 2
                    //
                    //
                    //
                    //               Scan each triangle in the box
                    //
                    for (m=1; m<=4; m++) {
                        m1 = m;
                        m2 = 0;
                        if (m!=4) {
                            m3 = m+1;
                        } else {
                            m3 = 1;
                        }

                        switch (castab[sh[m1]+1][sh[m2]+1][sh[m3]+1]) {
                            case 1: // Line between vertices 1 and 2
                                x1=xh[m1];
                                y1=yh[m1];
                                x2=xh[m2];
                                y2=yh[m2];
                                break;
                            case 2: // Line between vertices 2 and 3
                                x1=xh[m2];
                                y1=yh[m2];
                                x2=xh[m3];
                                y2=yh[m3];
                                break;
                            case 3: // Line between vertices 3 and 1
                                x1=xh[m3];
                                y1=yh[m3];
                                x2=xh[m1];
                                y2=yh[m1];
                                break;
                            case 4: // Line between vertex 1 and side 2-3
                                x1=xh[m1];
                                y1=yh[m1];
                                x2=xsect(m2,m3);
                                y2=ysect(m2,m3);
                                break;
                            case 5: // Line between vertex 2 and side 3-1
                                x1=xh[m2];
                                y1=yh[m2];
                                x2=xsect(m3,m1);
                                y2=ysect(m3,m1);
                                break;
                            case 6: //  Line between vertex 3 and side 1-2
                                x1=xh[m3];
                                y1=yh[m3];
                                x2=xsect(m1,m2);
                                y2=ysect(m1,m2);
                                break;
                            case 7: // Line between sides 1-2 and 2-3
                                x1=xsect(m1,m2);
                                y1=ysect(m1,m2);
                                x2=xsect(m2,m3);
                                y2=ysect(m2,m3);
                                break;
                            case 8: // Line between sides 2-3 and 3-1
                                x1=xsect(m2,m3);
                                y1=ysect(m2,m3);
                                x2=xsect(m3,m1);
                                y2=ysect(m3,m1);
                                break;
                            case 9: // Line between sides 3-1 and 1-2
                                x1=xsect(m3,m1);
                                y1=ysect(m3,m1);
                                x2=xsect(m1,m2);
                                y2=ysect(m1,m2);
                                break;
                            default: continue;

                        }
                        // Put your processing code here and comment out the printf
                        //printf("%f %f %f %f %f\n",x1,y1,x2,y2,z[k]);
                        render.addSegment(x1, y1, x2, y2);
                    }
                }
            }
        }
    }

    private double xsect(int p1, int p2){
        return (h[p2]*xh[p1]-h[p1]*xh[p2])/(h[p2]-h[p1]);
    }
    
    private double ysect(int p1, int p2){
        return (h[p2]*yh[p1]-h[p1]*yh[p2])/(h[p2]-h[p1]);
    }
}
