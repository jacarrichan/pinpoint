package com.navercorp.pinpoint.plugin.tomcat.interceptor;

import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.plugin.tomcat.TomcatConstants;
import com.process.ZoaThreadLocal;
import com.squareup.okhttp.Request;

/**
 * for okhttp
 */
public class RequestBuilderBuildMethodInterceptor implements AroundInterceptor {
    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());

    public RequestBuilderBuildMethodInterceptor() {
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
            ((Request.Builder) target).header(TomcatConstants.ACTION_KEY, ZoaThreadLocal.G_Ins().G_CInf());
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