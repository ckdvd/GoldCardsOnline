package com.tengman.db26.utils;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 色块提取轮廓
 */
public class ColorBlobRect {
    /**
     * 色调
     */
    private int thresholdH0 = 0;
    private int thresholdH1 = 180;
    /**
     * 饱和度
     */
    private int thresholdS0 = 0;
    private int thresholdS1 = 255;
    /**
     * 亮度
     */
    private int thresholdV0 = 0;
    private int thresholdV1 = 255;

    public int getThresholdH0() {
        return thresholdH0;
    }

    public void setThresholdH0(int thresholdH0) {
        this.thresholdH0 = thresholdH0;
    }

    public int getThresholdH1() {
        return thresholdH1;
    }

    public void setThresholdH1(int thresholdH1) {
        this.thresholdH1 = thresholdH1;
    }
    public int getThresholdS0() {
        return thresholdS0;
    }

    public void setThresholdS0(int thresholdS0) {
        this.thresholdS0 = thresholdS0;
    }

    public int getThresholdS1() {
        return thresholdS1;
    }

    public void setThresholdS1(int thresholdS1) {
        this.thresholdS1 = thresholdS1;
    }
    public int getThresholdV0() {
        return thresholdV0;
    }

    public void setThresholdV0(int thresholdV0) {
        this.thresholdV0 = thresholdV0;
    }

    public int getThresholdV1() {
        return thresholdV1;
    }

    public void setThresholdV1(int thresholdV1) {
        this.thresholdV1 = thresholdV1;
    }


    //    public Bitmap getRect(Mat src) {
    public Mat getRect(Mat src) {
        //2.颜色通道转换
        Mat hsv = new Mat();
// hue色调，saturation饱和度，value亮度
// HSV色域范围是H:0-179, S:0-255, V:0-255
        Imgproc.cvtColor(src, hsv, Imgproc.COLOR_RGB2HSV);
        //3.颜色检查的上限和下限
// 其中的两个Scalar 是hsv格式的颜色对象。
// 第一个是开始值，后面的是结束值。然后openCV就会在这两个颜色范围内进行分割。将属于该颜色范围的地方设置为白色。
// 不属于的就设置为黑色。
// PS：实在没办法，也可以通过openCV的 samples工程中的 color-blob-detection 示例代码。实现点击触摸获取当前图片的HSV颜色值。 ColorBlobDetector 类中，下面的方法可以打印看看结果值。
        Mat dst = new Mat();
        Scalar lowerb = new Scalar(thresholdH0, thresholdS0, thresholdV0);
        Scalar upperb = new Scalar(thresholdH1, thresholdS1, thresholdV1);
        Core.inRange(hsv, lowerb, upperb, dst);
//        if (dst.width() == src.width() && dst.height() == src.height()) {
//            threshold1 -= 1;
//            return getRect(src);
//        }
        hsv.release();
        return dst;
    }

    private Rect findMaxRect(Mat dst) {
        List<MatOfPoint> contours = new ArrayList<>(); //存储提取后的轮廓对象集合
        Mat hierarchy = new Mat();//起到一个提取过程中间转换暂存的作用。
        Imgproc.findContours(dst, contours, hierarchy, Imgproc.RETR_EXTERNAL
                , Imgproc.CHAIN_APPROX_NONE);//保存物体边界上所有连续的轮廓点到contours向量内；
        hierarchy.release();
//,Imgproc.CHAIN_APPROX_SIMPLE);//仅保存轮廓的拐点信息，把所有轮廓拐点处的点保存入contours向量内，拐点与拐点之间直线段上的信息点不予保留；
//         , Imgproc.CHAIN_APPROX_TC89_L1);//使用teh-Chinl chain 近似算法;
//,Imgproc.CHAIN_APPROX_TC89_KCOS);//使用teh-Chinl chain 近似算法。
        if (contours.size() == 0) {
//"轮廓提取失败"
            return null;
        }
// 6 最大面积区域提取
        // 查找最大面积
        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
        Rect rect = null;
        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea) {
                maxArea = area;
                rect = Imgproc.boundingRect(wrapper);//将该区域转为Rect矩形对象
            }
        }
        return rect;
    }

    @Override
    public String toString() {
        return "ColorBlobRect{" +
                "thresholdH0=" + thresholdH0 +
                ", thresholdH1=" + thresholdH1 +
                ", thresholdS0=" + thresholdS0 +
                ", thresholdS1=" + thresholdS1 +
                ", thresholdV0=" + thresholdV0 +
                ", thresholdV1=" + thresholdV1 +
                '}';
    }
}
