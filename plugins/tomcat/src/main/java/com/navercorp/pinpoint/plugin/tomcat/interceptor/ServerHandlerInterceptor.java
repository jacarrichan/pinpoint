package com.navercorp.pinpoint.plugin.tomcat.interceptor;

import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.plugin.tomcat.util.ServletHandletUtils;
import weblogic.servlet.internal.ServletRequestImpl;
import weblogic.servlet.internal.ServletResponseImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * for jetty7
 * Created by Jacarri on 2017-10-21.
 */
public class ServerHandlerInterceptor extends BaseReadCookieInterceptor {
    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());

    public ServerHandlerInterceptor(TraceContext traceContext) {
        super(traceContext);
    }

    @Override
    public void before(Object target, Object[] args) {
        logger.beforeInterceptor(target, args);
        final HttpServletRequest request = (ServletRequestImpl) args[1];
        final HttpServletResponse response = (ServletResponseImpl) args[2];
        try {
            ServletHandletUtils.bindCookieThreadlocal(request, response, traceContext);
        } catch (IOException t) {
            logger.warn("Failed to BEFORE process. {}", t.getMessage(), t);
        }
    }
}