/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isolevels;

/**
 *
 * @author likhachev
 */

import com.amd.aparapi.Range;
import com.amd.aparapi.device.Device;
import com.amd.aparapi.device.OpenCLDevice;
import com.amd.aparapi.opencl.OpenCL;
import com.amd.aparapi.opencl.OpenCL.Resource;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class Convolutor {
    @Resource("isolevels/convolution.cl") interface MyConvolution extends OpenCL<MyConvolution>{
        MyConvolution applyConvolution(//
            Range range, //
            @GlobalReadOnly("_convMatrix3x3") float[] _convMatrix3x3,//// only read from kernel 
            @GlobalReadOnly("_imagIn") byte[] _imageIn,// only read from kernel (actually char[])
            @GlobalWriteOnly("_imagOut") byte[] _imageOut, // only written to (never read) from kernel (actually char[])
            @Arg("_width") int _width,// 
            @Arg("_height") int _height);
    }

    final OpenCLDevice openclDevice = (OpenCLDevice) Device.best();        
    final MyConvolution convolution = openclDevice.bind(MyConvolution.class);  
   
    Convolutor() {
        System.out.print(MyConvolution.class.getCanonicalName());
    }
    
    void convolve(BufferedImage inputImage, BufferedImage outputImage) {
   
    final float convMatrix3x3[] = new float[] {
        0f, -10f,   0f,   
        -10f,  40f, -10f,
        0f, -10f,   0f,                        
    };
        
        
        byte []inBytes = ((DataBufferByte) inputImage.getRaster().getDataBuffer()).getData();
        byte []outBytes = ((DataBufferByte) outputImage.getRaster().getDataBuffer()).getData();
        
        applyConvolution(convMatrix3x3, inBytes, outBytes, inputImage.getWidth(), inputImage.getHeight());
      
    }
      
    Range range = null;

    protected void applyConvolution(float[] _convMatrix3x3, byte[] _inBytes, byte[] _outBytes, int _width, int _height) {

       
    
       if (range == null) {
          range = openclDevice.createRange(_width * _height * 3);
       }

       convolution.applyConvolution(range, _convMatrix3x3, _inBytes, _outBytes, _width, _height);
    }
         
}
