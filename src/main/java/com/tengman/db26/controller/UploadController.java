package com.tengman.db26.controller;

import ch.qos.logback.core.util.StringUtil;
import com.google.zxing.NotFoundException;
import com.tengman.db26.domain.*;
import com.tengman.db26.service.ImageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Slf4j
@RestController
public class UploadController {

    @Resource
    ImageService imageService;

    @RequestMapping("/result/uploadQr")
    public BaseResponse uploadQr(String appid, String appsecret, MultipartFile simplephoto) throws IOException {
        log.info(appid);
        log.info(appsecret);
        //1.保存图片
        String path = imageService.saveImage(simplephoto);
        if (StringUtil.notNullNorEmpty(path)) {
            try {
                log.info(path);
                String qrContent = imageService.getQRContent(path);
                log.info(qrContent);
                if (qrContent != null) {
                    DB26 db26 = imageService.parseQr(qrContent);
                    if (db26 != null) {
                        DB26Item db26Item = imageService.matchItem(db26);
                        if (db26Item != null) {
                            ChannelResult imageResult = imageService.getImageResult(path, db26Item);
                            String currentTimeFormatted = getCurrentTimeFormatted();
                            DB26Content db26Content = new DB26Content(qrContent, currentTimeFormatted, Collections.singletonList(imageResult), currentTimeFormatted);
                            return BaseResponse.success(db26Content);
                        } else {
                            return BaseResponse.fail("未知的项目");
                        }
                    } else {
                        return BaseResponse.fail("未知的编码规则");
                    }
                } else {
                    return BaseResponse.fail("二维码内容为识别");
                }
            } catch (NotFoundException e) {
                return BaseResponse.fail("未识别二维码");
            }
        } else {
            return BaseResponse.fail("上传失败");
        }
    }

    public String getCurrentTimeFormatted() {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 格式化时间
        return now.format(formatter);
    }
}
