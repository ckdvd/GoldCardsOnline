package com.tengman.db26.utils;

/**
 * 一维数组中寻找一个峰值
 *
 * @author liyang
 */
public class PeakFinder {
    private float valueABS;//阀值，波峰波谷的差值
    private float valueMax;// 波峰最小值

    public PeakFinder() {
    }

    public PeakFinder(float valueABS, float valueMax) {
        this.valueABS = valueABS;
        this.valueMax = valueMax;
    }

    /**
     * @param arr   数组
     * @param start 起始点
     * @param end   结束点
     * @return 分值极其索引值
     */
    public float[] findPeak(float[] arr, int start, int end) {
        float[] peak = {0, 0};

        // half the list
        // int step = ((end) - start) / 2;
        int step = 1;
        int midIndex = start + step;
//        LogUtil.d("midIndex: " + midIndex);
        if (midIndex == end) { // this means that we've reach the base case: single
            // elem remaining
            return peak;
        }
        // compare
        if (midIndex == -1 || midIndex == 0) {
            return peak;
        } else {
            float previous = arr[midIndex - 1];
            float next = arr[midIndex + 1];
            float v = arr[midIndex];
//            LogUtil.d("previous=" + previous);
//            LogUtil.d("next=" + next);
//            LogUtil.d("v=" + v);
            if (previous > v && v <= next) {//是峰值
//                LogUtil.d("valueMax:" + valueMax);
//                if (previous - v >= valueABS && next - v >= valueABS) {//差值够大
                if (valueMax - v > valueABS) {//差值够大
                    peak[0] = v;
//                    last_min = arr[midIndex];//更新最小值
                    peak[1] = midIndex;
                    return peak;
                }
                peak = findPeak(arr, midIndex, end);
            } else { // 小于前值或者小于后值：上坡或者小坡
//                if (arr[midIndex] < last_min) {
//                    last_min = arr[midIndex];//更新最小值
//                }
                peak = findPeak(arr, midIndex, end);
            }
        }
        return peak; // should never be reached
    }
}