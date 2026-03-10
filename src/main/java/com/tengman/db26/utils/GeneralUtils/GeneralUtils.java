package com.tengman.db26.utils.GeneralUtils;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * opencv的一些通用方法工具类
 */
public class GeneralUtils {

    private static final int BLACK = 0;
    private static final int WHITE = 255;

    // 设置归一化图像的固定大小
    private static final Size dsize = new Size(32, 32);

    /**
     * 作用：输入图像路径，返回mat矩阵
     *
     * @param imgPath 图像路径
     * @return
     */
    public static Mat matFactory(String imgPath) {
        return Imgcodecs.imread(imgPath);
    }

    /**
     * 作用：输入图像Mat矩阵对象，返回图像的宽度
     *
     * @param src Mat矩阵图像
     * @return
     */
    public static int getImgWidth(Mat src) {
        return src.cols();
    }

    /**
     * 作用：输入图像Mat矩阵，返回图像的高度
     *
     * @param src Mat矩阵图像
     * @return
     */
    public static int getImgHeight(Mat src) {
        return src.rows();
    }

    /**
     * 作用：获取图像(y,x)点的像素，我们只针对单通道(灰度图)
     *
     * @param src Mat矩阵图像
     * @param y   y坐标轴
     * @param x   x坐标轴
     * @return
     */
    public static int getPixel(Mat src, int y, int x) {
        return (int) src.get(y, x)[0];
    }

    /**
     * 作用：设置图像(y,x)点的像素，我们只针对单通道(灰度图)
     *
     * @param src   Mat矩阵图像
     * @param y     y坐标轴
     * @param x     x坐标轴
     * @param color 颜色值[0-255]
     */
    public static void setPixel(Mat src, int y, int x, int color) {
        src.put(y, x, color);
    }

    /**
     * 作用：保存图像
     *
     * @param src      Mat矩阵图像
     * @param filePath 要保存图像的路径及名字
     * @return
     */
    public static boolean saveImg(Mat src, String filePath) {
        return Imgcodecs.imwrite(filePath, src);
    }


    /**
     * 确保白底黑字或者黑底白字
     *
     * @param src
     * @param b   true：表示白底黑字 ， false相反
     * @return
     */
    public static Mat turnPixel(Mat src, boolean b) {
        if (src != null) {
            int width = GeneralUtils.getImgWidth(src);
            int height = GeneralUtils.getImgHeight(src);
            int value;
            int black_num = 0;
            int white_num = 0;
            int i, j;
            for (i = 0; i < width; i++) {
                for (j = 0; j < height; j++) {
                    value = GeneralUtils.getPixel(src, j, i);
                    if (value == GeneralUtils.getWHITE()) {
                        white_num++;
                    } else if (value == GeneralUtils.getBLACK()) {
                        black_num++;
                    }
                }
            }

            if (b && black_num > white_num) {
                //反转
                src = turnPixel(src);
            } else if (!b && white_num > black_num) {
                //反转
                src = turnPixel(src);
            }
        }
        return src;
    }

    /**
     * 作用：翻转图像像素
     *
     * @param src Mat矩阵图像
     * @return
     */
    public static Mat turnPixel(Mat src) {
        if (src.channels() != 1) {
            throw new RuntimeException("不是单通道图，需要先灰度话！！！");
        }
        int j, i, value;
        int width = getImgWidth(src), height = getImgHeight(src);
        for (j = 0; j < height; j++) {
            for (i = 0; i < width; i++) {
                value = getPixel(src, j, i);
                if (value == 0) {
                    setPixel(src, j, i, WHITE);
                } else {
                    setPixel(src, j, i, BLACK);
                }
            }
        }
        return src;
    }

    /**
     * 图像腐蚀/膨胀处理 腐蚀和膨胀对处理没有噪声的图像很有利，慎用
     */
    public static Mat erodeDilateImg(Mat src) {
        Mat outImage = new Mat();

        // size 越小，腐蚀的单位越小，图片越接近原图
        Mat structImage = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2));

        /**
         * 图像腐蚀 腐蚀说明： 图像的一部分区域与指定的核进行卷积， 求核的最`小`值并赋值给指定区域。 腐蚀可以理解为图像中`高亮区域`的'领域缩小'。
         * 意思是高亮部分会被不是高亮部分的像素侵蚀掉，使高亮部分越来越少。
         */
        Imgproc.erode(src, outImage, structImage, new Point(-1, -1), 2);
        src = outImage;

        /**
         * 膨胀 膨胀说明： 图像的一部分区域与指定的核进行卷积， 求核的最`大`值并赋值给指定区域。 膨胀可以理解为图像中`高亮区域`的'领域扩大'。
         * 意思是高亮部分会侵蚀不是高亮的部分，使高亮部分越来越多。
         */
        Imgproc.dilate(src, outImage, structImage, new Point(-1, -1), 2);
        src = outImage;

        return src;
    }

    /**
     * canny算法，边缘检测
     *
     * @param src
     * @return
     */
    public static Mat canny(Mat src) {
        Mat mat = src.clone();
        Imgproc.Canny(src, mat, 160, 240);
        return mat;
    }


    public static int getBLACK() {
        return BLACK;
    }

    public static int getWHITE() {
        return WHITE;
    }

    public static Size getDsize() {
        return dsize;
    }


    public static final int BLACK_VALUE_VERTICAL = 110;
    public static final int BLACK_VALUE_HORIZONTAL = 190;

    /**
     * 去除图片黑边，若无黑边，则原图返回。默认“全黑”阈值为 {@code BLACK_VALUE}
     *
     * @param srcMat 预去除黑边的Mat
     * @return 去除黑边之后的Mat
     */
    public static Mat removeBlackEdge(Mat srcMat) {
        return removeBlackEdge(srcMat, BLACK_VALUE_VERTICAL);
    }

    /**
     * 去除图片黑边，若无黑边，则原图返回。
     *
     * @param blackValue 一般低于5的已经是很黑的颜色了
     * @param srcMat     源Mat对象
     * @return Mat对象
     */
    public static Mat removeBlackEdge(Mat srcMat, int blackValue) {
        return removeBlackEdge(srcMat, blackValue, blackValue);
    }

    /**
     * @param srcMat      图片矩阵
     * @param blackValueH 水平方向阈值，均值小于改值就裁剪掉
     * @param blackValueV 垂直方向阈值，均值小于改值就裁剪掉
     * @return
     */
    public static Mat removeBlackEdge(Mat srcMat, int blackValueH, int blackValueV) {
        // 预截取，默认播放条等情况的处理
//        Mat smallMat = cut(srcMat, (int) (srcMat.width() * 0.02), (int) (srcMat.height() * 0.02));
        // 灰度
//        Mat grayMat =srcMat;
        Mat grayMat = gray(srcMat);
//        Mat grayMat = GrayUtils.grayColByAdapThreshold(srcMat);
        int topRow = 0;
        int leftCol = 0;
        int rightCol = grayMat.width() - 1;
        int bottomRow = grayMat.height() - 1;

        // 上方黑边判断
        for (int row = 0; row < grayMat.height(); row++) {
            // 判断当前行是否基本“全黑”，阈值自定义；
            if (sum(grayMat.row(row)) / grayMat.width() < blackValueV) {
                // 更新截取条件
                topRow = row;
            } else {
                break;
            }
        }
        // 左边黑边判断
        for (int col = 0; col < grayMat.width(); col++) {
            // 判断当前列是否基本“全黑”，阈值自定义；
            if (sum(grayMat.col(col)) / grayMat.height() < blackValueH) {
                // 更新截取条件
                leftCol = col;
            } else {
                break;
            }
        }
        // 右边黑边判断
        for (int col = grayMat.width() - 1; col > 0; col--) {
            // 判断当前列是否基本“全黑”，阈值自定义；
            if (sum(grayMat.col(col)) / grayMat.height() < blackValueH) {
                // 更新截取条件
                rightCol = col;
            } else {
                break;
            }
        }
        // 下方黑边判断
        for (int row = grayMat.height() - 1; row > 0; row--) {
            // 判断当前行是否基本“全黑”，阈值自定义；
            if (sum(grayMat.row(row)) / grayMat.width() < blackValueV) {
                // 更新截取条件
                bottomRow = row;
            } else {
                break;
            }
        }

        int x = leftCol;
        int y = topRow;
        int width = rightCol - leftCol;
        int height = bottomRow - topRow;

        if (leftCol == 0 && rightCol == grayMat.width() - 1 && topRow == 0 && bottomRow == grayMat.height() - 1) {
            return srcMat;
        }
        return cut(srcMat, x, y, width, height);
    }

    /**
     * 秦邦卡条自定义去黑边
     *
     * @param srcMat      图片矩阵
     * @param blackValueH 水平方向阈值，均值小于改值就裁剪掉
     * @param blackValueV 垂直方向阈值，均值小于改值就裁剪掉
     * @return
     */
    public static Mat removeBlackEdgeQB(Mat srcMat, int blackValueH, int blackValueV) {
        // 预截取，默认播放条等情况的处理
//        Mat smallMat = cut(srcMat, (int) (srcMat.width() * 0.02), (int) (srcMat.height() * 0.02));
        // 灰度
//        Mat grayMat =srcMat;
        Mat grayMat = gray(srcMat);
//        Mat grayMat = GrayUtils.grayColByAdapThreshold(srcMat);
        int topRow = 0;
        int leftCol = 0;
        int rightCol = grayMat.width() - 1;
        int bottomRow = grayMat.height() - 1;

        // 上方黑边判断
        for (int row = 0; row < grayMat.height(); row++) {
            // 判断当前行是否基本“全黑”，阈值自定义；
            if (sum(grayMat.row(row)) / grayMat.width() < blackValueV) {
                // 更新截取条件
                topRow = row;
            } else {
                break;
            }
        }
        // 下方黑边判断
        for (int row = grayMat.height() - 1; row > 0; row--) {
            // 判断当前行是否基本“全黑”，阈值自定义；
            if (sum(grayMat.row(row)) / grayMat.width() < blackValueV) {
                // 更新截取条件
                bottomRow = row;
            } else {
                break;
            }
        }
        //先把上下边减了，再来处理左右
        if (topRow > 0 || bottomRow < grayMat.height() - 1) {
            int maxTop = grayMat.height() / 3;//y
            if (topRow > maxTop) {
                topRow = maxTop;
            }
            if (bottomRow < maxTop * 2) {//x
                bottomRow = maxTop * 2;
            }
            topRow = topRow + maxTop / 6;//压缩矩形框，防止有毛刺
            bottomRow = bottomRow - maxTop / 6;
        }
        if (blackValueH > 0) {
            grayMat = cut(grayMat, 0, topRow, rightCol, bottomRow - topRow);
            // 左边黑边判断
            for (int col = 0; col < grayMat.width(); col++) {
                // 判断当前列是否基本“全黑”，阈值自定义；
                if (sum(grayMat.col(col)) / grayMat.height() < blackValueH) {
                    // 更新截取条件
                    leftCol = col;
//            } else {
                    break;
                }
            }
            // 右边黑边判断
            for (int col = grayMat.width() - 1; col > 0; col--) {
                // 判断当前列是否基本“全黑”，阈值自定义；
                if (sum(grayMat.col(col)) / grayMat.height() < blackValueH) {
                    // 更新截取条件
                    rightCol = col;
//            } else {
                    break;
                }
            }
        }
        int x = leftCol;
        int y = topRow;
        int width = rightCol - leftCol;
        int height = bottomRow - topRow;

        if (leftCol == 0 && rightCol == grayMat.width() - 1 && topRow == 0 && bottomRow == grayMat.height() - 1) {
            return srcMat;
        }
        return cut(srcMat, x, y, width, height);
    }

    /**
     * @param srcMat      mat
     * @param WhiteValueH
     * @return
     */
    public static Mat removeWhiteEdgeQB(Mat srcMat, int WhiteValueH) {
        // 预截取，默认播放条等情况的处理
//        Mat smallMat = cut(srcMat, (int) (srcMat.width() * 0.02), (int) (srcMat.height() * 0.02));
        // 灰度
//        Mat grayMat =srcMat;
        Mat grayMat = gray(srcMat);
//        Mat grayMat = GrayUtils.grayColByAdapThreshold(srcMat);
        int topRow = 0;
        int leftCol = 0;
        int rightCol = grayMat.width() - 1;
        int bottomRow = grayMat.height() - 1;

        // 左边白边判断
        for (int col = 0; col < grayMat.width(); col++) {
            if (sum(grayMat.col(col)) / grayMat.height() > WhiteValueH) {//是白色就更新截取条件
                leftCol = col;
            } else {//不是就停止
                break;
            }
        }
        // 右边白边判断
        for (int col = grayMat.width() - 1; col > 0; col--) {
            if (sum(grayMat.col(col)) / grayMat.height() > WhiteValueH) {//是白色就更新截取条件
                rightCol = col;
            } else {//不是就停止
                break;
            }
        }
        //没找到白边
        if (leftCol == 0 && rightCol == grayMat.width() - 1) {
            return grayMat;
        }
        //增加线条关联,系数（1~9）
        int paddingLeft = grayMat.width() / 9;
        if (leftCol > paddingLeft) {
            leftCol = paddingLeft;
        }
        int paddingRight = grayMat.width() / 9;
        if (rightCol < grayMat.width() - paddingRight) {
            rightCol = grayMat.width() - paddingRight;
        }
        int x = leftCol;
        int y = topRow;
        int width = rightCol - leftCol;
        int height = bottomRow - topRow;
        return cut(grayMat, x, y, width, height);
    }

    /**
     * 秦邦卡条自定义去黑边
     *
     * @param grayMat    图片矩阵
     * @param cThreshold C线阈值
     * @return
     */
    public static int getLineNumber(Mat grayMat, int cThreshold) {
        int linNumber = 0;
        for (int col = 0; col < grayMat.width(); col++) {
            // 判断当前列是否基本“全黑”，阈值自定义；
            if (sum(grayMat.col(col)) / grayMat.height() < cThreshold) {
                // 更新截取条件
                linNumber++;
            }
        }
        return linNumber;
    }

    public static int getCLineIndexQB(Mat grayMat, int cThreshold, int line) {
        int leftCol = 0;
        for (int col = 0; col < grayMat.width() / (line / 3f); col++) {
            // 判断当前列是否基本“全黑”，阈值自定义；
            if (sum(grayMat.col(col)) / grayMat.height() < cThreshold) {
                // 更新截取条件
                leftCol = col;
//            } else {
                break;
            }
        }
        return leftCol;
    }

    private static Mat cut(Mat srcMat, int x, int y, int width, int height) {
        return new Mat(srcMat, new Rect(x, y, width, height));

    }

    public static Mat gray(Mat srcMat) {
        Mat matTemp = new Mat(srcMat.size(), CvType.CV_8UC1);
        Imgproc.cvtColor(srcMat, matTemp, Imgproc.COLOR_RGB2GRAY);
        return matTemp;
    }

    /**
     * 求和
     *
     * @param mat mat
     * @return sum
     */
    private static int sum(Mat mat) {
        int sum = 0;
        for (int row = 0; row < mat.height(); row++) {
            for (int col = 0; col < mat.width(); col++) {
                sum += mat.get(row, col)[0];
            }
        }
        return sum;
    }

    /**
     * @param src graymat
     * @param n1  width
     * @param n2  height
     * @return
     */
    public static int[] getGrayArrayByMat(Mat src, int n1, int n2) {
        int[] xNum = new int[n1];
        for (int i = 0; i < n1; i++) {
            for (int j = 0; j < n2; j++) {
                xNum[i] += getPixel(src, j, i);
            }
        }
        return xNum;
    }
}
