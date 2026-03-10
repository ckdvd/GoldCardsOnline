package com.tengman.db26;

import org.bytedeco.javacpp.Loader;
import org.opencv.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Db26Application {

    public static void main(String[] args) {
        init();
        SpringApplication.run(Db26Application.class, args);
    }

    public static void init() {
        System.setProperty("org.bytedeco.javacpp.extract", "true");
        System.setProperty("org.bytedeco.javacpp.extractDir", "/tmp/javacpp-native-libs");

        try {
            Loader.load(org.bytedeco.opencv.global.opencv_core.class);
            Loader.load(org.bytedeco.opencv.global.opencv_imgproc.class);
            System.out.println("Native libraries loaded successfully");
        } catch (Exception e) {
            System.err.println("Failed to load native libraries: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
