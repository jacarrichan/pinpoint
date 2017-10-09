package com.navercorp.pinpoint.plugin.tomcat.aspect;

import com.navercorp.pinpoint.bootstrap.instrument.aspect.Aspect;
import com.navercorp.pinpoint.bootstrap.instrument.aspect.JointPoint;
import com.navercorp.pinpoint.bootstrap.instrument.aspect.PointCut;
import com.navercorp.pinpoint.plugin.tomcat.util.ServletHandletUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Jacarri on 2017-10-02.
 */
@Aspect
public abstract class ServerHandlerAspect {
    @PointCut
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int type) throws IOException, ServletException {
        ServletHandletUtils. bindCookieThreadlocal(request, response);
        if (ServletHandletUtils.handFilter(request, response)) {
            String msg = "请求参数中有发现{},将不会进入正常的业务流程";
            System.err.println(msg);
            response.getWriter().close();
            return;
        }else {
            __handle(target, request, response, type);
        }
    }

    @JointPoint
    public abstract void __handle(String target, HttpServletRequest request, HttpServletResponse response, int type) throws IOException, ServletException;

}
