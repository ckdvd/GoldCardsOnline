package com.tengman.db26.dao;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
public class ImageDao {

    public String saveImage(MultipartFile simplephoto) {
        try {
            String originalFilename = simplephoto.getOriginalFilename();
            int lastIndexOf = originalFilename.lastIndexOf(".");
            String extendName = originalFilename.substring(lastIndexOf);
            String dest = UUID.randomUUID().toString() + extendName;
            String pathname = "/Users/liyang/IdeaProjects/files/" + dest;
            simplephoto.transferTo(new File(pathname));
            return pathname;
        } catch (IOException e) {
            return null;
        }
    }
}
