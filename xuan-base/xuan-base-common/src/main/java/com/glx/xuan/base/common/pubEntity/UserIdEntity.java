package com.glx.xuan.base.common.pubEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName UserIdEntity
 * @Description TODO UserIdEntity
 * @Author Administrator
 * @Date 2021/3/30 15:36
 * @Version 1.0
 **/
@Data
@ApiModel(value="公共获取用户id实体", description="公共获取用户id实体")
public class UserIdEntity implements Serializable {
    /**
     *代理主键
     */
    @ApiModelProperty(value = "用户id" ,name = "userId", required = true)
    private String userId;
}
