package com.glx.xuan.base.common.constant;

/**
 * @author gaozepeng
 * @since 2022/7/25
 */
public class ProcessConstant {
    public static final String XML_PATH = "bpmn/";

    /**
     * 审批人集合变量名
     */
    public static final String ASSIGNEE_LIST = "assigneeList";

    /**
     * 审批人集合元素变量名：assignee
     */
    public static final String ASSIGNEE = "assignee";

    /**
     * 审批人变量名：${assignee}
     */
    public static final String VAR_ASSIGNEE = "${assignee}";


    public static final String BPMN_SUFFIX = ".bpmn";
}
