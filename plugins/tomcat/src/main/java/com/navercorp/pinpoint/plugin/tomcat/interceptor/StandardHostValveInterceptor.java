package com.navercorp.pinpoint.plugin.tomcat.interceptor;

import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.plugin.tomcat.util.ServletHandletUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * for tomcat
 */
public class StandardHostValveInterceptor extends BaseReadCookieInterceptor {
    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());

    public StandardHostValveInterceptor(TraceContext traceContext) {
        super(traceContext);
    }

    @Override
    public void before(Object target, Object[] args) {
        logger.beforeInterceptor(target, args);
        logger.debug("{},{},{},traceContext{}", this, target, args, traceContext);
        final HttpServletRequest request = (HttpServletRequest) args[0];
        final HttpServletResponse response = (HttpServletResponse) args[1];
        try {
            ServletHandletUtils.bindCookieThreadlocal(request, response, traceContext);
        } catch (IOException t) {
            logger.warn("Failed to BEFORE process. {}", t.getMessage(), t);
        }
    }

}