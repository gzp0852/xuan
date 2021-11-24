package com.glx.xuan.base.common.pubEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName SingleEntity
 * @Description TODO UserIdEntity
 * @Author Administrator
 * @Date 2021/5/30 15:36
 * @Version 1.0
 **/
@Data
@ApiModel(value="公共校验token实体", description="公共校验token实体")
public class SingleEntity {

    /**
     *token
     */
    @ApiModelProperty(value = "token" ,name = "token", required = true)
    private String token;

    /**
     *sig
     */
    @ApiModelProperty(value = "sig" ,name = "sig")
    private String sig;

    /**
     *userId
     */
    @ApiModelProperty(value = "userId" ,name = "userId")
    private String userId;
}
