
Simple image segmentation toy, tailored to biomedical, mostly nuclear medicine, images

The main purpose is to automate ROI creation over solid organs and pars such as liver or heard's ventricles.    
Isolevel creation is based on works of http://paulbourke.net/papers/conrec et al
while contouring algorithm spun off of 
http://cyberleninka.ru/article/n/algoritm-postroeniya-kontura-ploskoy-figury

Following thresholding methods available
1. Laplacian of Gaussian filter 
2. Otsu histogram              

It can read JPEG and DICOM images, for latter dcm4che is needed. 
Ensure you have [Roim](https://github.com/ivli/roim) library available.   

it is far from being complete but workin' 

Cheers,
I.
