package com.navercorp.pinpoint.plugin.app.interceptor;

import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.plugin.app.AppConstants;
import com.squareup.okhttp.Request;

import static com.navercorp.pinpoint.plugin.app.util.AppHandlerUtils.getUsernameFromTraceContent;

/**
 * for okhttp
 */
public class RequestBuilderBuildMethodInterceptor implements AroundInterceptor {
    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());
    private final TraceContext traceContext;

    public RequestBuilderBuildMethodInterceptor(TraceContext traceContext) {
        this.traceContext = traceContext;
    }

/*    public RequestBuilderBuildMethodInterceptor(TraceContext traceContext, MethodDescriptor methodDescriptor, InterceptorScope interceptorScope) {
    }*/

    @Override
    public void before(Object target, Object[] args) {
        logger.beforeInterceptor(target, args);
        logger.debug("{},{},{}",this,target,args);
        try {
            if (!(target instanceof Request.Builder)) {
                return;
            }
            final Request.Builder builder = ((Request.Builder) target);
            logger.debug("set Sampling flag=false");
            builder.header(AppConstants.ACTION_KEY, getUsernameFromTraceContent(traceContext));
            return;
        } catch (Throwable t) {
            logger.warn("Failed to BEFORE process. {}", t.getMessage(), t);
        }
    }

    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {
        logger.afterInterceptor(target, args);
    }
}