package com.tengman.db26.service;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.tengman.db26.dao.CheckItemDao;
import com.tengman.db26.dao.ImageDao;
import com.tengman.db26.domain.ChannelResult;
import com.tengman.db26.domain.DB26;
import com.tengman.db26.domain.DB26Item;
import com.tengman.db26.utils.ColorUtil;
import com.tengman.db26.utils.QrCodeUtil;
import com.tengman.db26.utils.TCRatioProcess;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import com.tengman.db26.utils.GeneralUtils.GeneralUtils;
import com.tengman.db26.utils.MyRectUtil;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ImageService {

    static {
    //         加载 动态链接库
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        Loader.load(opencv_core.class);
//        System.out.println(opencv_core.CV_VERSION());
    }

    String DB_26 = "^([0-9A-Z]{9})+([A-Z]{1}[0-9]{3})+([0-9]{4})+([0-9]{7})$";//24位

    @Resource
    ImageDao imageDao;
    @Resource
    CheckItemDao checkItemDao;

    /**
     * 图片本地存储
     * 可以考虑用阿里云服务器
     *
     * @param simplephoto
     * @return
     */
    public String saveImage(MultipartFile simplephoto) {
        return imageDao.saveImage(simplephoto);
    }

    public ChannelResult getImageResult(String path, DB26Item db26Item) {
        Mat src = Imgcodecs.imread(path);
        Mat cardOutline = getCardOutline(src);
        log.debug("liyang", "cardOutline=" + cardOutline);
        src.release();
        //裁剪出卡槽区域
        int rows = cardOutline.rows();
        int rowStart = rows * 5 / 14;//卡是7cm,用7的倍数比较容易理解
        int rowEnd = rows * 8 / 14;//卡是7cm,用7的倍数比较容易理解
        Mat submat = cardOutline.submat(rowStart, rowEnd, cardOutline.cols() - 9, cardOutline.cols() + 9);
        //转为灰度数组
        List<Float> grayArrayByMat = ColorUtil.getGrayArrayYMat(submat);//从上到下获取每行灰度值
        submat.release();
        ChannelResult channelResult = new ChannelResult();
        channelResult.setCodeName(db26Item.getName());
        channelResult.setCriterion(db26Item.getCriterion());
        channelResult.setGrayArray(grayArrayByMat);
        new TCRatioProcess(19, 10.1F).checkImage(channelResult);
        return channelResult;
    }

    /**
     * 获取二维码内容
     *
     * @param path
     * @return
     * @throws IOException
     * @throws NotFoundException
     */
    public String getQRContent(String path) throws IOException, NotFoundException {
        log.info("path={}", path);
        File file = new File(path);
        log.info("file={}", file);
        return QrCodeUtil.decodeQrCode(new File(path));
    }

    /**
     * 解析二维码内容
     *
     * @return
     */
    public DB26Item matchItem(DB26 db26) {
        Optional<DB26Item> first = checkItemDao.getItemList().stream().filter(DB26Item -> DB26Item.getCode().equals(db26.getItem())
        ).findFirst();
        return first.orElse(null);
    }

    public DB26 parseQr(String uniqueCode) {
        Matcher matcher = Pattern.compile(DB_26).matcher(uniqueCode);
        if (matcher.find()) {
            String code = matcher.group(1);
            String item = matcher.group(2);
            String date = matcher.group(3);
            String batch = matcher.group(4);
            log.info("code=" + code);
            log.info("item=" + item);
            log.info("date=" + date);
            log.info("batch=" + batch);
            return new DB26(code, item, date, batch);
        }
        return null;
    }

    private Mat getCardOutline(Mat matSrc) {
        return getCardOutline(matSrc, 1);
    }

    private Mat getCardOutline(Mat matSrc, int cardNum) {
//        bitmap.recycle();
        //2.获取亮度，根据亮度来设置提取阈值
        Mat gray = GeneralUtils.gray(matSrc);
        Scalar avg = Core.mean(gray);
        gray.release();
        int v = (int) avg.val[0];//亮度
        log.debug(String.format("v=%d", v));
        log.debug("cardNum=" + cardNum);
        //3.金标卡轮廓提取并裁剪
        return new MyRectUtil()
                .setCardNum(cardNum)
//                .setColorBlobThresholdH0(v / 6)
                //小卡图片灰度较低，所以要设置初始值高点，尽快圈出卡片轮廓
                .setColorBlobThresholdV0((v - cardNum))
                .getRecognizeCard(matSrc);
    }
}
