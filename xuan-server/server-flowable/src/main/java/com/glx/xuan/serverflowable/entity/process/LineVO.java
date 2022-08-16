package com.glx.xuan.serverflowable.entity.process;

import lombok.Data;

import java.util.List;

/**
 * @Auther rongchunjian
 * @Date 2021/3/25
 */

@Data
public class LineVO {
    private String id;

    private String name;

    private String dstId;

    private Integer priority;

    private String srcId;

    private List<List<ConditionGroupListVO>> conditionGroupList;
}
