package com.navercorp.pinpoint.plugin.tomcat.interceptor;


import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;
import com.navercorp.pinpoint.bootstrap.interceptor.scope.InterceptorScope;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.plugin.tomcat.TomcatConstants;
import com.process.ZoaThreadLocal;
import org.apache.http.HttpRequest;

/**
 * for  apache http-client4
 */
public class HttpRequestExecutorExecuteMethodInterceptor implements AroundInterceptor {
    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());

    public HttpRequestExecutorExecuteMethodInterceptor(TraceContext traceContext, MethodDescriptor methodDescriptor, InterceptorScope interceptorScope) {
    }

    @Override
    public void before(Object target, Object[] args) {
        logger.beforeInterceptor(target, args);
        final HttpRequest httpRequest = getHttpRequest(args);

        if (httpRequest != null) {
            httpRequest.setHeader(TomcatConstants.ACTION_KEY, ZoaThreadLocal.G_Ins().G_CInf());
        }
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