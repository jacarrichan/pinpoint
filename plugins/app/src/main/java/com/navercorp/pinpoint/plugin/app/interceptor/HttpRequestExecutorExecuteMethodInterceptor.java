package com.navercorp.pinpoint.plugin.app.interceptor;


import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.plugin.app.AppConstants;
import org.apache.http.HttpRequest;

import static com.navercorp.pinpoint.plugin.app.util.AppHandlerUtils.getUsernameFromTraceContent;

/**
 * for  apache http-client4
 */
public class HttpRequestExecutorExecuteMethodInterceptor implements AroundInterceptor {
    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());
    private final TraceContext traceContext;


    public HttpRequestExecutorExecuteMethodInterceptor(TraceContext traceContext/*, MethodDescriptor methodDescriptor, InterceptorScope interceptorScope*/) {
        this.traceContext = traceContext;
    }

    @Override
    public void before(Object target, Object[] args) {
        logger.beforeInterceptor(target, args);
        final HttpRequest httpRequest = getHttpRequest(args);
        if (null == httpRequest ) {
            logger.warn("无法获取HttpRequest，不能将username放在http的请求头中");
            return;
        }
        httpRequest.setHeader(AppConstants.ACTION_KEY, getUsernameFromTraceContent(traceContext));
        return;
    }

    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {
        logger.afterInterceptor(target, args);
    }

    private HttpRequest getHttpRequest(Object[] args) {
        if (args != null && args.length >= 1 && args[0] != null && args[0] instanceof HttpRequest) {
            return (HttpRequest) args[0];
        }

        return null;
    }
}