package com.navercorp.pinpoint.plugin.container.aspect;

import com.navercorp.pinpoint.bootstrap.instrument.aspect.Aspect;
import com.navercorp.pinpoint.bootstrap.instrument.aspect.JointPoint;
import com.navercorp.pinpoint.bootstrap.instrument.aspect.PointCut;
import com.navercorp.pinpoint.plugin.container.util.ServletHandlerUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * for jetty 7
 * Created by Jacarri on 2017-10-02.
 */
@Aspect
public abstract class ServerHandlerAspect {
    @PointCut
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int type) throws IOException, ServletException {
        if (!ServletHandlerUtils.process(request, response)) {
            __handle(target, request, response, type);
        }
    }

    @JointPoint
    public abstract void __handle(String target, HttpServletRequest request, HttpServletResponse response, int type) throws IOException, ServletException;

}
