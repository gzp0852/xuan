package com.glx.xuan.base.common.pubEntity;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


/**
 * 通用分页查询对象
 *
 */
@Data
@ApiModel(value = "分页查询公共对象")
public class PageEntity<T> implements Serializable {
	
	private static final long serialVersionUID = -2685260689939112464L;
	
	@ApiModelProperty(value = "当前页码",required = true)
    private int pageNum = 1;
	
	@ApiModelProperty(value = "一页显示记录数",required = true)
	@NotBlank(message = "一页显示记录数不能为空")
    private int pageSize = 5;

    @ApiModelProperty(value = "用户id",required = true)
    private String userId;

    @ApiModelProperty(value = "角色名称",required = true)
    private String roleName;
	
    @ApiModelProperty(value = "数据实体")
    private transient  T entity;

}
