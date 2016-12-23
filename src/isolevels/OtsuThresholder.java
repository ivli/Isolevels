
package isolevels;

import java.util.Arrays;

public class OtsuThresholder {
    private int histData[];
    private int maxLevelValue;
    private int threshold;
    private final static int HISTOGRAM_SIZE = 0x10000;//Short.MAX_VALUE;
    private final static int DATA_MASK = 0xFFFF;

    public OtsuThresholder() {
        histData = new int[HISTOGRAM_SIZE];
    }

    public int getThreshold() {
        return threshold;
    }

    public int compute(int[] srcData) {           
        // Clear histogram data
        // Set all values to zero

        Arrays.fill(histData, 0);
        // Calculate histogram and find the level with the max value
        // Note: the max level value isn't required by the Otsu method
        int ptr = 0;
        maxLevelValue = 0;

        while (ptr < srcData.length) {
            int h = DATA_MASK & srcData[ptr];
            ++histData[h];
            
            if (histData[h] > maxLevelValue) 
                maxLevelValue = histData[h];
            ++ptr;
        }

        // Total number of pixels
        final int total = srcData.length;

        float sum = 0;
        for (int t=0; t < histData.length; ++t) 
            sum += t * histData[t];

        float sumB = 0;
        int wB = 0;
        int wF = 0;

        float varMax = 0;
        threshold = 0;

        for (int t=0; t<histData.length; ++t) {            
            wB += histData[t]; // Weight Background
            if (wB == 0) continue;

            wF = total - wB; // Weight Foreground
            if (wF == 0) break;

            sumB += (float) (t * histData[t]);

            float mB = sumB / wB; // Mean Background
            float mF = (sum - sumB) / wF; // Mean Foreground

            // Calculate Between Class Variance
            float varBetween = (float)wB * (float)wF * (mB - mF) * (mB - mF);	

            // Check if new maximum found
            if (varBetween > varMax) {
                    varMax = varBetween;
                    threshold = t;
            }
        }

        return threshold;
    }
        /*
	public void Apply(int[] aData){
            if (aData != null) {		
                int ptr = 0;
                while (ptr < srcData.length)
                {
                        aData[ptr] = ((0xFF & srcData[ptr]) >= threshold) ? (byte) 255 : 0;
                        ptr ++;
                }
            }
	}
        */
}
