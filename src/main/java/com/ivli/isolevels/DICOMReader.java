/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.isolevels;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;



import javax.imageio.spi.IIORegistry;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReaderSpi;
import org.dcm4che3.io.DicomInputStream;

/**
 *
 * @author likhachev
 */
public class DICOMReader { 
    static {
        //ImageIO.scanForPlugins();
        //installReader();
    }

    static ImageReader installReader() {
        javax.imageio.ImageReader ir = ImageIO.getImageReadersByFormatName("DICOM").next(); //NOI18N

        if (null == ir) {            
            IIORegistry registry = IIORegistry.getDefaultInstance();
            registry.registerServiceProvider(new DicomImageReaderSpi());
            ir = ImageIO.getImageReadersByFormatName("DICOM").next();  //NOI18N
        }

        return ir;
    }
        
    static ImageReader ImageReader(final String aFile) throws IOException {                       
        ImageReader ret = installReader();
        ret.setInput(ImageIO.createImageInputStream(new File(aFile))); 
        return ret;        
    }    
}
