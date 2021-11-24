package com.glx.xuan.base.common.pubEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName UserEntity
 * @Description TODO UserEntity
 * @Author Administrator
 * @Date 2021/3/30 15:36
 * @Version 1.0
 **/
@Data
@ApiModel(value="公共获取用户实体", description="公共获取用户实体")
public class UserEntity implements Serializable {
    /**
     *代理主键
     */
    @ApiModelProperty(value = "用户id" ,name = "userId", required = true)
    private String userId;

    @ApiModelProperty(value = "用户名称",required = true)
    private String userName;
}
