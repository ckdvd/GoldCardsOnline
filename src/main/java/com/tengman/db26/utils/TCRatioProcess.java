package com.tengman.db26.utils;

/**
 * T/C比值法
 * Created by hzw on 2018/6/25.
 */
public class TCRatioProcess extends CheckProcess {

    public TCRatioProcess(float minReference, float tThreshold) {
        super(minReference, tThreshold);
    }

    @Override
    protected float getCalcResult(float t, float c) {
        return t / c;
    }


}
