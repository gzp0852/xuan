package com.glx.xuan.base.common.model;

/**
 * @ClassName PageModel
 * @Description TODO PageModel
 * @Author Administrator
 * @Date 2021/3/25 11:16
 * @Version 1.0
 **/

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 通用返回对象
 *
 * 使用方式 PageModel.getPage("11","1",result);
 */
@ApiModel(value = "分页返回公共类")
public class PageModel<T> {
    @ApiModelProperty(value = "总记录数")
    private String total;
    @ApiModelProperty(value = "当前页码")
    private String curPage;
    @ApiModelProperty(value = "具体数据")
    private List<T> rows;

    public PageModel() {
    }

    protected PageModel(String total, String curPage, List<T> rows) {
        this.total = total;
        this.curPage = curPage;
        this.rows = rows;
    }

    /**
     * 返回分页相关数据
     *
     * @param rows 获取的数据
     */
    public static <T> PageModel<T> getPage(String total, String curPage, List<T> rows) {
        return new PageModel<T>(total, curPage, rows);
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCurPage() {
        return curPage;
    }

    public void setCurPage(String curPage) {
        this.curPage = curPage;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
