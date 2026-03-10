package com.tengman.db26.utils;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import com.tengman.db26.utils.GeneralUtils.GeneralUtils;
import com.tengman.db26.utils.old.utils.HandleImgUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
@Slf4j
public class MyRectUtil {
    private static final int MAX_V = 230;
    private int minWidth = 90;
    private int minHeight = 190;
    private int maxWidth = 90;
    private int maxHeight = 139;
    //算子
    private CannyRect cannyRect;
    private final ColorBlobRect colorBlobRect;

    @Getter
    private boolean rotate;
    private boolean isSingle;
    private int cardNum = 1;

    public MyRectUtil() {
        colorBlobRect = new ColorBlobRect();
    }

    public MyRectUtil(CannyRect recProcess) {
        cannyRect = recProcess;
        colorBlobRect = new ColorBlobRect();
    }

    public MyRectUtil setRotate(boolean rotate) {
        this.rotate = rotate;
        return this;
    }

    public MyRectUtil setCardNum(int cardNum) {
        this.cardNum = cardNum;
        return this;
    }

    public MyRectUtil setSingle(boolean isSingle) {
        this.isSingle = isSingle;
        return this;
    }

    public boolean isSingle() {
        return isSingle || cardNum == 1;
    }
//
//    public MyRectUtil setOnlyCanny(boolean onlyCanny) {
//        this.onlyCanny = onlyCanny;
//        return this;
//    }

    public MyRectUtil setCannyThreshold0(int threshold0) {
        if (cannyRect != null) {
            this.cannyRect.setThreshold0(threshold0);
            log.debug("liyang", String.format("setCannyThreshold0=%d", threshold0));
        }
        return this;
    }

    public MyRectUtil setCannyThreshold1(int threshold1) {
        if (cannyRect != null) {
            this.cannyRect.setThreshold1(threshold1);
            log.debug("liyang", String.format("setCannyThreshold1=%d", threshold1));
        }
        return this;
    }

    public MyRectUtil setColorBlobThresholdH0(int threshold0) {
        log.debug("liyang", String.format("setColorBlobThresholdH0=%d", threshold0));
        this.colorBlobRect.setThresholdH0(threshold0);
        return this;
    }

//    public MyRectUtil setColorBlobThresholdH1(int threshold1) {
//        log.debug("liyang", String.format("setColorBlobThresholdH1=%d", threshold1));
//        this.colorBlobRect.setThresholdH1(threshold1);
//        return this;
//    }

    public MyRectUtil setColorBlobThresholdS0(int threshold0) {
        log.debug("liyang", String.format("setColorBlobThresholdS0=%d", threshold0));

        this.colorBlobRect.setThresholdS0(threshold0);
        return this;
    }

    public MyRectUtil setColorBlobThresholdS1(int threshold1) {
        log.debug("liyang", String.format("setColorBlobThresholdS1=%d", threshold1));
        this.colorBlobRect.setThresholdS1(threshold1);
        return this;
    }

    public MyRectUtil setColorBlobThresholdV0(int threshold0) {
        threshold0 = Math.min(MAX_V, threshold0);
        log.debug("liyang", String.format("setColorBlobThresholdV0=%d", threshold0));
        this.colorBlobRect.setThresholdV0(threshold0);
        return this;
    }

    public MyRectUtil setColorBlobThresholdV1(int threshold1) {
        threshold1 = Math.min(233, threshold1);
        log.debug("liyang", String.format("setColorBlobThresholdV1=%d", threshold1));
        this.colorBlobRect.setThresholdV1(threshold1);
        return this;
    }

    public void setMinWidth(int minWidth) {
        this.minWidth = minWidth;
    }

    public MyRectUtil setMaxHeight(int height) {
        maxHeight = height;
        return this;
    }

    public MyRectUtil setMaxWidth(int width) {
        maxWidth = width;
        return this;
    }

    public void setMinHeight(int minHeight) {
        this.minHeight = minHeight;
    }

    /**
     * 识别卡的区域
     *
     * @param src
     * @return
     * @throws Exception
     */
    public Mat getRecognizeCard(Mat src) {
        log.info("liyang", "get outline start cardNum=" + cardNum);
        int width = src.width();
        int height = src.height();
        log.info("liyang", "src widthxheight=" + width + "x" + height);
        Mat dst = null;
        Mat result = null;
//        Imgproc.dilate(src, src, new Mat());
        //色块提取
        dst = colorBlobRect.getRect(src);
//        //轮廓提取
        result = getCardMat(src, dst);
        if (cardNum <= 3) {//1-3联卡
            int maxColorThreshold0 = Math.min(colorBlobRect.getThresholdV0() + 20, MAX_V);
//            maxWidth = (int) (width / 2 * Math.sqrt(cardNum) + cardNum * 3);
            maxWidth = width - cardNum;
            log.info("liyang", "MAX_WIDTH =" + maxWidth);
            while ((result == null || result.width() > maxWidth || result.height() < result.width() * (4 - cardNum)) && colorBlobRect.getThresholdV0() < maxColorThreshold0) {
                setColorBlobThresholdV0(colorBlobRect.getThresholdV0() + 1);
                dst = colorBlobRect.getRect(src);
                result = getCardMat(src, dst);
                log.debug("liyang", "isSingle result =" + result);
            }
        } else {//4联卡以上
            int minColorThreshold0 = colorBlobRect.getThresholdV0() - 10;
            if (cardNum > 6) {
                minWidth = width * 3 / 4;
            } else {
                minWidth = width * cardNum / 6;
            }
            minHeight = height * 3 / 4;
            log.info("liyang", "MIN_WIDTH =" + minWidth);
            log.info("liyang", "MIN_HEIGHT =" + minHeight);
            //通过增加Threshold1，循环获取得到满意的矩形框，canny算子不需要，没有变化
            while ((result == null || result.width() < minWidth || result.height() < result.width()) && colorBlobRect.getThresholdV0() > minColorThreshold0) {
                setColorBlobThresholdV0(colorBlobRect.getThresholdV0() - 1);
                dst = colorBlobRect.getRect(src);
                result = getCardMat(src, dst);
                log.debug("liyang", "result =" + result);
            }
        }
        if (result == null) {//未识别的情况
            log.error("liyang", "outline get rect failed");
//            int dwidth = 0;
//            dwidth = (int) (width / 2.5f * Math.sqrt(cardNum) + cardNum * 3);
//            if (cardNum >= 4) {
//                dwidth += 40;
//            }
//            dwidth = Math.min(dwidth, width);
//            int colStart = (src.width() - dwidth) / 2;
////            src.submat(0, src.height(), colStart, src.width() - colStart);
//            Mat temp = new Mat(src, new Rect(colStart, 0, dwidth, src.height()));
//            Mat gray = GeneralUtils.gray(temp);
//            Scalar avg = Core.mean(gray);
//            gray.release();
//            int v = (int) avg.val[0];//亮度
//            log.info("liyang", "but get card v: " + v);
//            if (v > 180) {
//                return temp;
//            }
        }
        log.info("liyang", "get outline end");
        return result;
    }

    private Mat getCardMat(Mat src, Mat dst) {
        if (dst.width() > 0) {//有轮廓
            RotatedRect rect = HandleImgUtils.findMaxRect(dst);
            if (rect != null) { // 旋转矩形
                Rect rect1 = rect.boundingRect();
                log.debug("liyang", "boundingRect=" + rect1);
//                if (rect1.x > 0 || ((rect1.x + rect1.width) < src.width())) {
                if (rect1.x > 0 || rect1.y > 0) {
                    return HandleImgUtils.cutRect(rect, src, 0, 0);
                }
            }
        }
        return null;
    }

    /**
     * 缩小法
     * 截取图片得到感兴趣的区域(每个通道的卡槽区域)
     *
     * @param src 通道裁切原图
     */
    public Mat getRecognizeChannel(Mat src) {
        int width = src.width();
        int height = src.height();
        log.debug("liyang", String.format("getRecognizeMat src size=%dx%d", width, height));
        //设置最小，最大宽度
        int maxH = height * 3 / 4;
        if (maxHeight < maxH) {
            maxHeight = maxH;
        }
        minHeight = height / 2;
        minWidth = height / 5;
        log.debug("liyang", String.format("MAX_HEIGHT=%d", maxHeight));
        maxWidth = minWidth * 4;

        Mat dst = null;
        Mat result = null;
        if (cannyRect != null) {
            dst = cannyRect.getRect(src);
            log.debug("liyang", String.format("dst size=%dx%d", dst.width(), dst.height()));
            result = getChanneMat(src, dst);
            log.debug("liyang", "result" + result);
            dst.release();
        } else {
//            log.error("liyang", "cannyRect get rect failed");
//            //        1.降噪 我们执行完毕inRange方法后，就能得到色块了。但是复杂环境下会有一些噪点。我们可以通过膨胀算法进行降噪。
//// 有两种方法：1 通过morphologyEx进行。
//// Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
//// Imgproc.morphologyEx(src, src, Imgproc.MORPH_OPEN, kernel);//进行开运算
//// 有两种方法： 2 通过dilate方法进行
//        Imgproc.dilate(src, src, new Mat());
            //色块提取
            dst = colorBlobRect.getRect(src);
            log.debug("liyang", String.format("dst size=%dx%d", dst.width(), dst.height()));
//        //轮廓提取
            result = getChanneMat(src, dst);
            log.debug("liyang", "result=" + result);
            int MIN_COLOR_THRESHOLD1 = colorBlobRect.getThresholdV1() - 30;
            //通过增加Threshold1，循环获取得到满意的矩形框，canny算子不需要，没有变化
            while ((result == null || result.height() > maxHeight || result.height() < minHeight
                    || result.width() > maxWidth || result.width() < minWidth)
                    && colorBlobRect.getThresholdV1() > MIN_COLOR_THRESHOLD1) {
                setColorBlobThresholdV1(colorBlobRect.getThresholdV1() - 1);
                dst = colorBlobRect.getRect(src);
                result = getChanneMat(src, dst);
                log.debug("liyang", "result =" + result);
            }
            dst.release();
        }
        //特殊情况如：卡面不清洁，整体颜色偏暗)给它一个中间矩形框
        if (result == null || result.height() > maxHeight || result.height() < minHeight
                || result.width() > maxWidth || result.width() < minWidth) {
            int left = width / 2 - 9;
            if (left < 1) left = 1;
            int top = height / 4;
            result = src.submat(top, height - top, left, width - left);
        }
        //调整大小
        Mat mat = resizeChannelResult(result, cannyRect == null);
        if (mat != null) {
            log.debug("liyang", String.format("colorBlobRect get rect success size=%dx%d", mat.width(), mat.height()));
            src.release();
            return mat;
        }
        log.error("liyang", "channel get rect failed");
        src.release();
        return null;
    }

    /**
     * 放大法
     * 截取图片得到感兴趣的区域(每个通道的卡槽区域)
     *
     * @param src 通道裁切原图
     */
    public Mat getRecognizeChannel2(Mat src) {
        int width = src.width();
        int height = src.height();
        log.debug("liyang", String.format("getRecognizeMat src size=%dx%d", width, height));
        //设置最小，最大宽度
        if (minWidth < width / 4) {
            minWidth = width / 4;
        }
        log.debug("liyang", "minWidth=" + minWidth);
        maxWidth = minWidth + minWidth;
        //设置最大高度
        minHeight = height / 4;
        maxHeight = minHeight * 3;
        Mat dst = null;
        Mat result = null;
        if (cannyRect != null) {
            dst = cannyRect.getRect(src);
            log.debug("liyang", String.format("dst size=%dx%d", dst.width(), dst.height()));
            result = getChanneMat(src, dst);
            log.debug("liyang", "result" + result);
            dst.release();
        } else {
            //色块提取
            dst = colorBlobRect.getRect(src);
            log.debug("liyang", String.format("dst size=%dx%d", dst.width(), dst.height()));
//        //轮廓提取
            result = getChanneMat(src, dst);
            log.debug("liyang", "result=" + result);
            int MAX_COLOR_THRESHOLD1 = Math.min(MAX_V, colorBlobRect.getThresholdV1() + 20);

            //通过增加Threshold1，循环获取得到满意的矩形框：大于最小宽度，
            while ((result == null || result.width() < minWidth || result.height() < minHeight) && colorBlobRect.getThresholdV1() < MAX_COLOR_THRESHOLD1) {
                setColorBlobThresholdV1(colorBlobRect.getThresholdV1() + 1);
                dst = colorBlobRect.getRect(src);
                result = getChanneMat(src, dst);
                log.debug("liyang", "result =" + result);
            }
            dst.release();
        }
        //特殊情况如：卡面不清洁，整体颜色偏暗)给它一个中间矩形框
        if (result == null || result.height() > maxHeight) {
            int left = width / 2 - 9;
            if (left < 1) left = 1;
            int top = height / 3;
            result = src.submat(top, height - top - 9, left, width - left);
        }
        //调整大小
        Mat mat = resizeChannelResult(result, cannyRect == null);
        if (mat != null) {
            log.debug("liyang", String.format("colorBlobRect get rect success size=%dx%d", mat.width(), mat.height()));
            src.release();
            return mat;
        }
        log.error("liyang", "channel get rect failed");
        src.release();
        return null;
    }

    /**
     * 裁剪大小:获取中间一小条试纸区域
     *
     * @param result
     * @return
     */
    private Mat resizeChannelResult(Mat result) {
        return resizeChannelResult(result, true);
    }

    private Mat resizeChannelResult(Mat result, boolean height) {
        if (result != null) {
            Mat gray = GeneralUtils.gray(result);
            Scalar avg = Core.mean(gray);
            log.debug("liyang", "result avg=" + avg);

            int width1 = result.width();
            int height1 = result.height();
            int height2 = result.height() / 2;
            log.debug("liyang", String.format("result size=%dx%d", width1, height1));
            if (height1 <= maxHeight && height1 > width1) {//符合要求：高度小于最大高度，且大于宽度*2
                int left = width1 / 2 - 9;//获取中间一小条
                if (left < 1) left = 1;
                int top = 0;
                Mat submat;
//                if (height) {
//                    top = height1 / 5;//去除卡槽边缘斜坡部分
//                    submat = result.submat(top, height1 - top, left, width1 - left);
//                } else {
//                top = height1 / 4;//去除卡槽边缘斜坡部分
//                for (; top < height2; top++) {
//                    double v = gray.get(top, left)[0];
//                    log.debug("liyang", "brightness=" + v);
//                }
                top = height1 / 5;//去除卡槽边缘斜坡部分
                submat = result.submat(top, height1 - top * 2 / 3, left, width1 - left);
//                }
                result.release();
                return submat;
            }
        }
        return null;
    }

    /**
     * 轮廓提取
     *
     * @param src 原图
     * @param dst 通过 colorblob/canny 得到的图
     * @return 返回 用dst得到的最大轮廓图
     */
    private Mat getChanneMat(Mat src, Mat dst) {
        if (dst.height() > 0) {//没有轮廓
//            RotatedRect rect = ContoursUtils.findMaxRect(dst);
            RotatedRect rect = HandleImgUtils.findMaxRect(dst);
            if (rect != null) { // 旋转矩形
                Rect rect1 = rect.boundingRect();
                log.debug("liyang", "boundingRect rect1.y= " + rect1.y);
                if (rect1.y < 1 || rect1.y > src.height() / 3) {//左边起点没有识别到直接舍弃
                    return null;
                }
                Mat mat = null;
                if (rotate) {
                    Mat CorrectImg = HandleImgUtils.rotation(dst, rect);
                    Mat NativeCorrectImg = HandleImgUtils.rotation(src, rect);
                    if (CorrectImg.width() > 0 && NativeCorrectImg.width() > 0) {
                        mat = HandleImgUtils.cutRect(CorrectImg, NativeCorrectImg);
                    } else {
                        mat = HandleImgUtils.cutRect(dst, src);
                    }
                } else {
                    mat = HandleImgUtils.cutRect(rect, src);
                }
                return mat;
            }
        }
        return null;
    }
}
