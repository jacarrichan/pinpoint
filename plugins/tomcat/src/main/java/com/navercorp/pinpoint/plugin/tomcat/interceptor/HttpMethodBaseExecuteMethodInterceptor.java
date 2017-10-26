package com.navercorp.pinpoint.plugin.tomcat.interceptor;

import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.plugin.tomcat.TomcatConstants;
import com.navercorp.pinpoint.plugin.tomcat.util.ServletHandletUtils;
import org.apache.commons.httpclient.HttpMethod;

/**
 * for common-httpclient3
 */
public class HttpMethodBaseExecuteMethodInterceptor implements AroundInterceptor {

    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());
    private final boolean isDebug = logger.isDebugEnabled();
    private final TraceContext traceContext;

    public HttpMethodBaseExecuteMethodInterceptor(TraceContext traceContext/*, MethodDescriptor methodDescriptor, InterceptorScope interceptorScope*/) {
        this.traceContext = traceContext;
    }

    @Override
    public void before(Object target, Object[] args) {
        logger.debug("{},{},{},{}",this,target,args,traceContext);
        logger.beforeInterceptor(target, args);
        // set http header for trace.
        setHttpTraceHeader(target, args);
    }


    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {
        logger.afterInterceptor(target, args);
    }


    private void setHttpTraceHeader(final Object target, final Object[] args) {
        if (target instanceof HttpMethod) {
            final HttpMethod httpMethod = (HttpMethod) target;
            httpMethod.setRequestHeader(TomcatConstants.ACTION_KEY, ServletHandletUtils.getUsernameFromTraceContent(traceContext));
        }
    }

}