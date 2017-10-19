package com.navercorp.pinpoint.plugin.tomcat.aspect;

import com.navercorp.pinpoint.bootstrap.instrument.aspect.Aspect;
import com.navercorp.pinpoint.bootstrap.instrument.aspect.JointPoint;
import com.navercorp.pinpoint.bootstrap.instrument.aspect.PointCut;
import com.navercorp.pinpoint.plugin.tomcat.util.ServletHandletUtils;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * for undertow(widefly)
 * Created by Jacarri on 2017-10-19.
 */
@Aspect
public abstract class ServletHandlerAspect {
    @PointCut
    public void handleRequest(HttpServerExchange exchange) throws IOException, ServletException {
        ServletRequestContext servletRequestContext = (ServletRequestContext) exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
        ServletRequest request = servletRequestContext.getServletRequest();
        ServletResponse response = servletRequestContext.getServletResponse();
        if (!ServletHandletUtils.process((HttpServletRequest) request, (HttpServletResponse) response)) {
            __handleRequest(exchange);
        }
    }

    @JointPoint
    public abstract void __handleRequest(HttpServerExchange exchange) throws IOException, ServletException;
}
