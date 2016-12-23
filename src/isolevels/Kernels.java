/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isolevels;

import java.awt.image.Kernel;

/**
 *
 * @author likhachev
 */
public class Kernels {
    static final float[] emboss = { -2,0,0,   0,1,0,   0,0,2 };
    static final float[] blurring = { 1f/9f,1f/9f,1f/9f, 1f/9f,1f/9f,1f/9f, 1f/9f,1f/9f,1f/9f };
    static final float[] sharpening = { -1,-1,-1,   -1,9,-1,   -1,-1,-1 };

    static final float[] Gauss_5x5 = { 1,  4,  7,  4, 1,
                               4, 16, 26, 16, 4,
                               7, 26, 41, 26, 7,
                               4, 16, 26, 16, 4,
                               1,  4,  7,  4, 1
    };

    static final float[] Gauss_3x3_sigma1 = {0.077847f, 0.123317f, 0.077847f, 
                                      0.123317f, 0.195346f, 0.123317f, 
                                      0.077847f, 0.123317f, 0.077847f};

    static final float[] Gauss_3x3_sigma3 = {0.107035f, 0.113092f, 0.107035f,
                                      0.113092f, 0.119491f, 0.113092f,
                                      0.107035f, 0.113092f, 0.107035f};

    static final float[] Laplacian_3x3 = { 0, -1,  0,
                                    -1,  4, -1,
                                    0, -1,  0};    

    static final float[] Laplacian_3x3_sigma_1point4 = { -0.0037480f,  -0.0073566f,  -0.0037480f,
                                                  -0.0073566f,  -0.0127458f,  -0.0073566f,
                                                  -0.0037480f,  -0.0073566f,  -0.0037480f};

    static final float[] Laplacian_11x11_sigma_2 = new float[] { 
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

    static final float[] Laplacian_7x7_sigma_1and4 = new float[] {
        2.502123e-004f, 5.777487e-004f, 8.316200e-004f, 8.967564e-004f, 8.316200e-004f, 5.777487e-004f, 2.502123e-004f, 
        5.777487e-004f, 9.295234e-004f, 5.289226e-004f, 5.056474e-005f, 5.289226e-004f, 9.295234e-004f, 5.777487e-004f, 
        8.316200e-004f, 5.289226e-004f, -2.021333e-003f, -3.967426e-003f, -2.021333e-003f, 5.289226e-004f, 8.316200e-004f, 
        8.967564e-004f, 5.056474e-005f, -3.967426e-003f, -6.873873e-003f, -3.967426e-003f, 5.056474e-005f, 8.967564e-004f, 
        8.316200e-004f, 5.289226e-004f, -2.021333e-003f, -3.967426e-003f, -2.021333e-003f, 5.289226e-004f, 8.316200e-004f, 
        5.777487e-004f, 9.295234e-004f, 5.289226e-004f, 5.056474e-005f, 5.289226e-004f, 9.295234e-004f, 5.777487e-004f, 
        2.502123e-004f, 5.777487e-004f, 8.316200e-004f, 8.967564e-004f, 8.316200e-004f, 5.777487e-004f, 2.502123e-004f}; 
    
    final static float[] LOG_5x5 = {-1f, -1f, -1f, -1f, -1f, 
                                    -1f, -1f, -1f, -1f, -1f, 
                                    -1f, -1f, 24f, -1f, -1f, 
                                    -1f, -1f, -1f, -1f, -1f, 
                                    -1f, -1f, -1f, -1f, -1f};
    
   /// final static Kernel LOG55 = new Kernel(5,5, LOG_5x5);
}
