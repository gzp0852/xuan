package com.glx.xuan.serverflowable.service;

import java.util.Map;

/**
 * @author gaozepeng
 * @since 2022/7/25
 */
public interface ProcessService {
    Map deployByXML(String xmlName);

    Map startProcess(String processKey);
}
