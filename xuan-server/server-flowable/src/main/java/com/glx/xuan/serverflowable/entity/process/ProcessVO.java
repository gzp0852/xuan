package com.glx.xuan.serverflowable.entity.process;

import lombok.Data;

import java.util.List;

/**
 * @Auther rongchunjian
 * @Date 2021/4/18
 */

@Data
public class ProcessVO {

    private String processName;

    private List<LineVO> lineList;

    private List<NodeVO> nodeList;
}
