package com.navercorp.pinpoint.plugin.container.interceptor;

import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.plugin.container.util.ServletHandlerUtils;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * for undertow(widefly)
 * Created by Jacarri on 2017-10-19.
 */
public class ServletHandlerInterceptor extends BaseReadCookieInterceptor {
    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());

    public ServletHandlerInterceptor(TraceContext traceContext) {
        super(traceContext);
    }

    @Override
    public void before(Object target, Object[] args) {
        logger.beforeInterceptor(target, args);
        HttpServerExchange exchange = (HttpServerExchange) args[0];
        ServletRequestContext servletRequestContext = exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
        ServletRequest request = servletRequestContext.getServletRequest();
        ServletResponse response = servletRequestContext.getServletResponse();
        try {
            ServletHandlerUtils.bindRequestThreadlocal((HttpServletRequest) request, (HttpServletResponse) response, traceContext);
        } catch (IOException t) {
            logger.warn("Failed to BEFORE process. {}", t.getMessage(), t);
        }
    }
}