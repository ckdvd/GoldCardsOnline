package com.tengman.db26.utils;


import com.tengman.db26.utils.old.utils.HandleImgUtils;
import org.opencv.core.Mat;

/**
 * 边缘检测提取轮廓
 */
public class CannyRect {
    private int threshold0 = 0;
    /**
     * 经验值：[65-75]
     */
    private int threshold1 = 75;

    public int getThreshold0() {
        return threshold0;
    }

    public void setThreshold0(int threshold0) {
        this.threshold0 = threshold0;
    }

    public int getThreshold1() {
        return threshold1;
    }

    public void setThreshold1(int threshold1) {
        this.threshold1 = threshold1;
    }

    public Mat getRect(Mat src) {
//        Mat src = GeneralUtils.matFactory(filePath);
//2.边缘检测和校正
        Mat cannyMat = HandleImgUtils.canny(src, threshold0, threshold1);
        return cannyMat;
    }
}
