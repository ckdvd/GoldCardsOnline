package com.tengman.db26.utils;


import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PeakFindUtil {

    public static List<Integer> getPeakIndex(List<Float> dataY, float max) {
//        float max = Collections.max(dataY);
//        float max = Collections.max(dataY);
        log.debug("max=" + max);
//        float valueMin = (max + max) / 3;
        float abs = max * 0.06f;
        abs = Math.min(abs, 50);
        PeakFinder peakFinder = new PeakFinder(abs, max);
        int loopLen = dataY.size();//每次1/1个点寻峰
//        int loopLen = dataY.size() / 2;//每次1/2个点寻峰
        List<Integer> peaklist = new ArrayList<>();
        log.debug("dataY size=" + dataY.size());
        float[] dest = copy2Array(dataY, 0, loopLen);
        log.debug("dest size=" + dest.length);
        for (int i = 0; i < dataY.size() - 2; ) {
            float[] peak = peakFinder.findPeak(dest, i, dest.length - 1);
            if (peak[0] != 0) {//值
                int e = (int) (peak[1]);//索引
                log.debug("正向寻峰: " + e + "," + peak[0]);
                peaklist.add(e);
                i = e + 1;//如果寻到峰就用当前点做下一个寻峰的起点
            } else {
                //没有寻到峰就
//						System.out.println("not Found a peak: " + i + "," + (i + loopLen));
                i = i + loopLen / 2;
            }
        }
//        //2.从右往左寻峰
//        PeakFinder.last_min = 50;//一次寻峰完要重置，
//        float[] reverseArray = reverseArray2(dataY);
//        int reverseloopLen = 16;//每次拿16个点寻峰
//        List<Integer> reversepeaklist = new ArrayList<>();
//        for (int i = 0; i <= reverseArray.length - reverseloopLen; ) {
//            float[] dest = new float[reverseloopLen];
//            System.arraycopy(reverseArray, i, dest, 0, reverseloopLen);
//            float[] peak = PeakFinder.findPeak(dest, 0, reverseloopLen - 1);
//            if (peak[0] != 0) {//值
//                int e = (int) (i + peak[1]);
//                int index = reverseArray.length - 1 - e;
//                i = e;
//                log.debug("逆向寻峰: " + index + "," + peak[0]);
//                reversepeaklist.add(index);
//            } else {
////						System.out.println("not Found a peak: " + i + "," + (i + loopLen));
//                i = i + reverseloopLen / 2;
//            }
//        }
//        //3.取两个集合的交集
//        peaklist.retainAll(reversepeaklist);
        //4.对交集结果转换成原始x坐标索引
//        List<Float> resultList = new ArrayList<>();
//        for (int i = 0; i < peaklist.size(); i++) {
//            float x = dataX.get(peaklist.get(i));
//            System.out.println("x=" + x);
//            resultList.add(x);
//
//        }
        return peaklist;
    }

    private static float[] reverseArray2(List<Float> old_array) {
        float[] new_array = new float[old_array.size()];
        for (int i = 0; i < old_array.size(); i++) {
            // 反转后数组的第一个元素等于源数组的最后一个元素：
            new_array[i] = old_array.get(old_array.size() - i - 1);
        }
        return new_array;
    }

    private static float[] copy2Array(List<Float> old_array, int start, int length) {
        float[] new_array = new float[length];
        int index = 0;
        for (int i = start; i < start + length; i++) {
            // 反转后数组的第一个元素等于源数组的最后一个元素：
            new_array[index++] = old_array.get(i);
        }
        return new_array;
    }
}
