package com.tengman.db26.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tengman.db26.domain.DB26Item;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Repository
public class CheckItemDao {

    public List<DB26Item> getItemList() {
        ObjectMapper objectMapper = new ObjectMapper(); // 创建ObjectMapper实例
        try {
            // 读取JSON文件并转换为User对象
//            String file = getClass().getClassLoader().getResource("user.json").getFile();
            File file = new ClassPathResource("db26.json").getFile();
            return objectMapper.readValue(file, new TypeReference<List<DB26Item>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
