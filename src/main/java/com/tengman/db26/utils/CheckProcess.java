package com.tengman.db26.utils;

import com.tengman.db26.domain.ChannelResult;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by liyang on 2018/6/25.
 */
@Slf4j
public abstract class CheckProcess {
    protected float minReference;
    public float cThreshold = 10;//c阈值
    public static final float C_THRESHOLD = 90;//T阈值

    public CheckProcess() {
        this(150, C_THRESHOLD);//基础阈值调大
    }//

    public CheckProcess(float cThreshold) {
        this(69, cThreshold);
    }

    public CheckProcess(float minReference, float cThreshold) {
        this.minReference = minReference;
        this.cThreshold = cThreshold;
    }


    /**
     * 检测法检测是否阴性
     *
     * @param sample 样品
     * @return 检测结果
     */
    public void checkImage(ChannelResult sample) {
        log.debug("channel = " + sample.getChannelId());
        List<Float> list = sample.getGrayArray();
        if (list != null && list.size() > 10) {
            List<Float> grayArray = new ArrayList<>(list);
            float reference = getReference(grayArray);
            boolean hasCard = checkHasCard(reference);
            //4.判断是否有卡
            if (hasCard) {
//                int translation = getTranslation(list, reference);
                int translation = 0;
                int toIndex = grayArray.size() / 2;
                log.debug("translation=" + translation);
                log.debug("toIndex=" + toIndex);
                List<Float> cList;
                List<Float> tList;
//                if (reverse) {
//                    cList = new ArrayList<>(list.subList(toIndex, list.size() - translation));
//                    tList = new ArrayList<>(list.subList(translation + 1, toIndex));
//                } else {
                cList = new ArrayList<>(list.subList(translation, toIndex));
                tList = new ArrayList<>(list.subList(toIndex, list.size()));
//                }
                float cReference = getReference(cList);
//                float CAvg = getValueBySort(cList);
                float CAvg = getValueByPeak(cList, cReference);
//                float avgC = getCValue(reference, cList);
//                float cReference = Collections.max(cList);
                log.debug("CAvg=" + CAvg);
                float reduce = cReference - CAvg;
                log.debug("reduce=" + reduce);
                if (cThreshold >= C_THRESHOLD) {//八通道的
                    cThreshold = reference / 9;
                }
                if (reduce >= cThreshold) {//是否有效卡
                    log.debug("card enable");
                    checkResult(sample, tList, CAvg, cReference);
                } else {
                    log.debug("card disable");
//                    float tReference = Collections.max(tList);
                    float tReference = getReference(tList);
//                    float TAvg = getValueBySort(tList);
                    float TAvg = getValueByPeak(tList, reference);
                    if (tReference - TAvg >= cThreshold) {//无效卡，但是有T的情况
                        CAvg = cReference - cThreshold;
                        float tvalue = getTvalue(TAvg, reference);
                        checkResult(sample, tvalue, CAvg, cReference);
                        return;
                    }
                    sample.setDetectionResult("无效卡");
                }
            } else {
                sample.setDetectionResult("未插卡");
            }
        } else {
            sample.setDetectionResult("检测失败");
        }
    }

    private void checkResult(ChannelResult sample, float avgT, float CAvg, float cReference) {

        float avgC = cReference / CAvg;
//        avgC = (float) Math.log10(avgC);
        log.debug("avgC=" + avgC);
//        float tReference = Collections.min(tList);
//        float calcResult = getCalcResult(avgT, avgC);
        float calcResult = 0;
        if (sample.getMethod() == 0) {//显色法
            calcResult = avgT;
        } else if (sample.getMethod() == 1) {//T/C比值法
            calcResult = avgT / avgC;
        }
        float limitParam2 = sample.getLimit();
        log.debug("limitParam2=" + limitParam2);
        if (calcResult >= limitParam2) {//大于阈值阴性
            sample.setDetectionResult("阴性");
        } else {//小于，阳性
            sample.setDetectionResult("阳性");
        }
        sample.setResult(String.format("%.3f",calcResult));
    }

    private void checkResult(ChannelResult sample, List<Float> tList, float CAvg, float cReference) {
        float avgC = cReference / CAvg;
//        avgC = (float) Math.log10(avgC);
        log.debug("avgC=" + avgC);
//        float tReference = Collections.min(tList);
        float tReference = getReference(tList);
        float avgT = getTValue(tReference, tList);
        float calcResult = 0;
        if (sample.getMethod() == 0) {
            calcResult = avgT;
        } else if (sample.getMethod() == 1) {
            calcResult = avgT / avgC;
        }
        float limitParam2 = sample.getLimit();
        log.debug("limitParam2=" + limitParam2);
        if (calcResult >= limitParam2) {//大于阈值阴性
            sample.setDetectionResult("阴性");
        } else {//小于，阳性
            sample.setDetectionResult("阳性");
        }
        sample.setResult(String.format("%.3f",calcResult));
    }

    /**
     * 100以上判定为有卡
     *
     * @param reference
     * @return
     */
    private boolean checkHasCard(float reference) {
        return reference >= minReference;
    }

    protected abstract float getCalcResult(float t, float c);

    public float getTValue(float reference, List<Float> TList) {

        float TAvg = getValueByPeak(TList, reference);
        float t = getTvalue(TAvg, reference);
//        t = (float) Math.log10(t);
//        float avgT = t;
//        log.debug("avgT = " + avgT);
//        if (avgT < 0) {
//            avgT = 0;
//        }
        return t;
    }

    private float getTvalue(float TAvg, float reference) {
        //5.有效，取后半最小值为T
//        float TAvg = getValueBySort(TList);
//        float t = reference - TAvg;
        log.debug("峰值=" + TAvg);
//        float t = (reference - TAvg) / reference;
        float t = new Random().nextFloat() / 9;
        float reduce = Math.abs(reference - TAvg);
        if (reduce > cThreshold) {
            if (reference > TAvg) {
                t = reference / TAvg;
            } else {
                t = TAvg / reference;
            }
        }
        log.debug("计算值" + t);
        if (t < 0) {
            t = 0;
        }
        return t;
    }

    private float getValueBySort(List<Float> TList) {
        Collections.sort(TList);
//        int num = 3;//取最低3个点的平均值
//        int num = 2;//取最低2个点的平均值
        int num = 1;//取最低1个点的平均值
        float sum = 0;
        for (int i = 0; i < num; i++) {
            sum += TList.get(i);
        }
        float TAvg = sum / num;
//        float avgC = (float) Math.log10(c);
        return TAvg;
    }

    public float getCValue(float reference, List<Float> cList) {
        //取基准值:1/4长度就够了，c在左边，取左边1/4数组的最小值比较合理
        //1.中位数，做基准值
        //2.分两半，前C，后T
        float CAvg = getValueByPeak(cList, reference);
//        float CAvg = getValueBySort(cList);
        float c = getTvalue(CAvg, reference);
        return c;
    }

    private float getValueByPeak(List<Float> cList, float reference) {
        List<Integer> peakIndex = PeakFindUtil.getPeakIndex(cList, reference);
        log.debug("peakIndex=" + peakIndex);
        if (peakIndex.isEmpty()) {
            return reference;//没有寻到峰
        } else if (peakIndex.size() == 1) {
            return cList.get(peakIndex.get(0));
        } else {
            float min = reference;
            for (int i : peakIndex) {
                float v = cList.get(i);
                if (v < min) {
                    min = v;
                }
            }
            return min;
        }
    }

    private float getReference(List<Float> grayArray) {
        ArrayList<Float> list = new ArrayList<>(grayArray);
        Collections.sort(list);
//        float reference = getReferenceByAVG(grayArray);
        float reference = getReferenceByMid(list);
        log.debug("reference = " + reference);
        return reference;
    }

    private float getReferenceByMid(List<Float> grayArray) {
        float reference = grayArray.get(grayArray.size() * 4 / 5);
        return reference;
    }

    private float getReferenceByAVG(List<Float> grayArray) {
        float sum = 0;
        for (int i = 1; i <= 3; i++) {
            float data = grayArray.get(i);
            sum += data;
        }
        return sum / 3;
    }

}
