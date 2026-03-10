package com.tengman.db26.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DB26Content {
    String codeID;
    String detectionDate;
    List<ChannelResult> detentionResult;
    String timestamp;
}
