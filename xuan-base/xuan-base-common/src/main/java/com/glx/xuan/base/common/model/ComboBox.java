package com.glx.xuan.base.common.model;

import java.util.List;

/**
 * ConboBox实体类
 * @author lmh
 * 创建日期：2020-03-31 10:12:32<br/>
 * 历史修订：<br/>
 */
public class ComboBox {

    /**
     * ID
     */
    private String key;

    /**
     * VALUE
     */
    private String title;

    /**
     * Code
     */
    private String value;

    /**
     * 父ID
     */
    //private String parentId;

    /**
     * 子节点
     */
    private List<ComboBox> children;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ComboBox> getChildren() {
        return children;
    }

    public void setChildren(List<ComboBox> children) {
        this.children = children;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ComboBox{" +
                "key='" + key + '\'' +
                ", title='" + title + '\'' +
                ", value='" + value + '\'' +
                ", children=" + children +
                '}';
    }
}
