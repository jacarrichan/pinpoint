package com.navercorp.pinpoint.plugin.container.interceptor;

import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.plugin.container.util.ServletHandlerUtils;

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
        final HttpServletRequest request = (HttpServletRequest) args[2];
        final HttpServletResponse response = (HttpServletResponse) args[3];
        try {
            ServletHandlerUtils.bindRequestThreadlocal(request, response, traceContext);
        } catch (IOException t) {
            logger.warn("Failed to BEFORE process. {}", t.getMessage(), t);
        }
    }
}