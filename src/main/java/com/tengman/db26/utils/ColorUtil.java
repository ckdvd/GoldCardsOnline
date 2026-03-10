package com.tengman.db26.utils;


import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyang on 2018/6/25.
 */

public class ColorUtil {
    // hzw from http://www.kmhpromo.com/rgbtopms/cmyktopantone.html
    // h(0-360),s(0-1),v(0-1)
    public static ArrayList<Integer> rgb2hsv(int r, int g, int b) {
        float h, s, v, min, delta;

        if (r > g) {
            v = Math.max(r, b);
            min = Math.min(g, b);
        } else {
            v = Math.max(g, b);
            min = Math.min(r, b);
        }

        delta = v - min;

        if (v == 0) {
            s = 0;
        } else {
            s = delta / v;
        }

        if (s == 0) {
            h = 0;
        } else {
            if (r == v) {
                h = (int) (60.0f * (g - b) / delta);
            } else if (g == v) {
                h = (int) (120 + 60.0f * (b - r) / delta);
            } else {
                h = (int) (240 + 60.0f * (r - g) / delta);
            }

            if (h < 0.0) {
                h += 360.0;
            }

            if (h > 360.0) {
                h -= 360.0;
            }
        }

        h = Math.round(h);
        s = Math.round(s * 255);
        v = Math.round(v);

        /* avoid the ambiguity of returning different values for the same color */
        if (h == 360) {
            h = 0;
        }

        ArrayList<Integer> hsv = new ArrayList<Integer>();
        hsv.add((int) h);
        hsv.add((int) s);
        hsv.add((int) v);

        return hsv;
    }

    // hue(0-360),saturation(0-255),value(0-255)
    public static ArrayList<Integer> hsv2rgb(double hue, double saturation, double value) {
        double h, s, v, h_temp;
        double f, p, q, t;
        double i;

        if (saturation == 0) {
            hue = value;
            saturation = value;
            value = value;
        } else {
            h = hue;
            s = saturation / 255.0f;
            v = value / 255.0f;

            if (h == 360) {
                h_temp = 0;
            } else {
                h_temp = h / 60;
            }
            i = Math.floor(h_temp);
            f = h_temp - i;
  /*
  p = v * (1.0 - s);
  q = v * (1.0 - (s * f));
  t = v * (1.0 - (s * (1.0 - f)));
  */
            double vs = v * s;
            p = value - value * s;

            switch ((int) i) {
                case 0:
                    t = v - vs * (1 - f);
                    hue = Math.round(value);
                    saturation = Math.round(t * 255.0);
                    value = Math.round(p);
                    break;

                case 1:
                    q = v - vs * f;
                    hue = Math.round(q * 255.0);
                    saturation = Math.round(value);
                    value = Math.round(p);
                    break;

                case 2:
                    t = v - vs * (1 - f);
                    hue = Math.round(p);
                    saturation = Math.round(value);
                    value = Math.round(t * 255.0);
                    break;

                case 3:
                    q = v - vs * f;
                    hue = Math.round(p);
                    saturation = Math.round(q * 255.0);
                    value = Math.round(value);
                    break;

                case 4:
                    t = v - vs * (1 - f);
                    hue = Math.round(t * 255.0);
                    saturation = Math.round(p);
                    value = Math.round(value);
                    break;

                case 5:
                    q = v - vs * f;
                    hue = Math.round(value);
                    saturation = Math.round(p);
                    value = Math.round(q * 255.0);
                    break;
            }
        }

        ArrayList<Integer> rgb = new ArrayList<Integer>();
        rgb.add((int) hue);
        rgb.add((int) saturation);
        rgb.add((int) value);

        return rgb;
    }

    // hzw
    // #define FLT_EPSILON     1.192092896e-07F        /* smallest such that 1.0+FLT_EPSILON != 1.0 */
    public static final double FLT_EPSILON = 1.192092896e-07F;
    public static final double CV_PI = 3.1415926535897932384;

    // otst大津法求阈值
    public static double getThreshVal_Otsu_8u(final Mat _src) {
        Size size = _src.size();
        final int N = 256;
        int i, j;
        int[] h = new int[N];

        double[] src;

        for (i = 0; i < size.height; i++) {
            for (j = 0; j < size.width; j++) {
                src = _src.get(i, j);
                if (src.length == 1) {
                    h[(int) (src[0])]++;
                } else if (src.length >= 3) {
                    h[RGB2GRAY((int) src[0], (int) src[1], (int) src[2])]++;
                }
            }
        }

        double mu = 0, scale = 1. / (size.width * size.height);
        for (i = 0; i < N; i++) {
            mu += i * (double) h[i];
        }

        mu *= scale;
        double mu1 = 0, q1 = 0;
        double max_sigma = 0, max_val = 0;

        for (i = 0; i < N; i++) {
            double p_i, q2, mu2, sigma;

            p_i = h[i] * scale;
            mu1 *= q1;
            q1 += p_i;
            q2 = 1. - q1;

            if (Math.min(q1, q2) < FLT_EPSILON || Math.max(q1, q2) > 1. - FLT_EPSILON)
                continue;

            mu1 = (mu1 + i * p_i) / q1;
            mu2 = (mu - q1 * mu1) / q2;
            sigma = q1 * q2 * (mu1 - mu2) * (mu1 - mu2);
            if (sigma > max_sigma) {
                max_sigma = sigma;
                max_val = i;
            }
        }
        return max_val;
    }

    public static int RGB2GRAY(int r, int g, int b) {
        return (b + g * 6 + r * 3) / 10;
    }

    public static int RGB2GRAY(double r, double g, double b) {
        return (int) ((b * 1 + g * 6 + r * 3) / 10);
//        int i = (int) (g + b - r);
//        if (i < 1) i = 1;
//        return i;
    }

    /**
     * 计算图像每列有线的点的个数数组
     *
     * @param m_image 图像mat
     * @return 图像每列有线的点的个数数组
     */
    public static List<Float> getGrayArrayHMat(Mat m_image) {
        int xlen = m_image.width();
        int ylen = m_image.height();
        int x, y;
        List<Float> grayArray = new ArrayList<>();
        try {
            for (x = 0; x < xlen; x++) {
                float gray = 0;
                for (y = 0; y < ylen; y++) {
                    double[] data = m_image.get(y, x);

                    if (m_image.channels() == 1) {
                        gray += (int) data[0];
                    } else if (m_image.channels() >= 3) {
                        gray += RGB2GRAY(data[0], data[1], data[2]);
                    }
                }
                grayArray.add(gray / ylen);
//                grayArray[x] = gray;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return grayArray;
    }

    /**
     * 计算图像每行有线的点的个数数组
     *
     * @param m_image
     * @return
     */
    public static List<Float> getGrayArrayYMat(Mat m_image) {
        int xlen = m_image.width();
        int ylen = m_image.height();

        int x, y;
        List<Float> grayArray = new ArrayList<>();
        try {
            for (y = 0; y < ylen; y++) {
                float gray = 0;
                for (x = 0; x < xlen; x++) {
                    double[] data = m_image.get(y, x);
                    if (m_image.channels() == 1) {
                        gray += (int) data[0];
                    } else if (m_image.channels() >= 3) {
                        gray += RGB2GRAY(data[0], data[1], data[2]);
                    }
                }
                grayArray.add(gray / xlen);
//                grayArray[x] = gray;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return grayArray;
    }

    /**
     * 计算图像每列有线的点的个数数组
     *
     * @param m_image  图像mat
     * @param m_thresh 阀值
     * @return 图像每列有线的点的个数数组
     */
    public static int[] OnProcessGraphDataExtraction(Mat m_image, int m_thresh, int gray_difference) {
        int xlen = m_image.width();
        int ylen = m_image.height();

        int m_Xmin = 0;
        int m_Xmax = 100;
        int m_Ymin = 0;
        int m_Ymax = 100;

        if (0 == xlen || 0 == ylen) {
            return null;
        }

        int x, y, ymin, ymax, npoints;

        String s = "";
        String s2 = "";

        // hzw lines
       /*
        s = ("x");
        s += ("\tymin\tymax");
        s += ("\tyavg");
        s += ("\r\n");
        */
        s = ("x");
        s += ("\tcount");
        s += ("\r\n");

        npoints = 0;
        float gray = 0;

        // hzw lines
        int[] points = new int[m_image.cols()]; // 二值化有黑点的条数,索引代表宽度(cols)所在的像素点位置,值代表超过阈值的点数(如果值超过高度(rows)的80%即判断为一条线)

        try {
            float[] grayArray = new float[xlen];
            for (x = 0; x < xlen; x++) {

                // hzw lines
                points[x] = 0;
                grayArray[x] = 0;
                for (y = 0; y < ylen; y++) {
                    double[] data = m_image.get(y, x);

                    if (m_image.channels() == 1) {
                        gray = (int) data[0];
                    } else if (m_image.channels() >= 3) {
                        gray = RGB2GRAY(data[0], data[1], data[2]);
                    } else {
                        return null;
                    }

                    if (x <= xlen * 0.45) {
                        //  gray += ((20 - gray_difference) * 2);
                        //  Log.e(TAG, "gray_difference: ==" + gray_difference + " gray: ==" + gray + " m_thresh -
                        // 4==" + (m_thresh - 4));
                    }
                    // hzw lines
                    //   if (gray < m_thresh) break;
                    if (gray <= m_thresh - gray_difference) {
                        points[x]++;
                    }
                }
                grayArray[x] = gray / ylen;
                s2 = String.format(("%d\t%d"), x, points[x]);
                s += s2;

                s += "\r\n";

                // hzw lines
            /*
            ymin = y;

            for (y = ylen-1; y > ymin; y--) {
                double [] data = m_image.get(y, x);

                if (m_image.channels() == 1) {
                    gray = (int)data[0];
                } else if (m_image.channels() >= 3) {
                    gray = RGB2GRAY (data[0], data[1], data[2]);
                } else {
                    return false;
                }

                if (gray < m_thresh) break;
            }

            ymax = y;

            if (ymin < ylen) {
                npoints++;

                float ftmp = x*(m_Xmax-m_Xmin)/xlen + m_Xmin;

                s2 = String.format(("%f"), ftmp);
                s += s2;

                {
                        ftmp = ymin*(m_Ymax-m_Ymin)/ylen + m_Ymin;
                        s2 = String.format(("\t%f"), ftmp);
                        s += s2;
                        ftmp = ymax*(m_Ymax-m_Ymin)/ylen + m_Ymin;
                        s2 = String.format(("\t%f"), ftmp);
                        s += s2;
                }

                {
                        ftmp = 0.5f*(ymin+ymax)*(m_Ymax-m_Ymin)/ylen + m_Ymin;
                        s2 = String.format(("\t%f"), ftmp);
                        s += s2;
                }

                s += "\r\n";
            }
            */
            }
        } catch (Exception e) {
            e.printStackTrace();
//            final String error = e.toString();
//            MiniCheckingActivity.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(MiniCheckingActivity.this, "hzw2 OnProcessGraphDataExtraction \n" + error, Toast
//                            .LENGTH_SHORT).show();
//                }
//            });

            return null;

        }
        return points;
    }
}
