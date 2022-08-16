package com.glx.xuan.serverflowable.service;

import javax.servlet.http.HttpServletResponse;

public interface TestService {
    public void genProcessDiagram(HttpServletResponse httpServletResponse, String processId);
}
