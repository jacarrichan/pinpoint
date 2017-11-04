package com.navercorp.pinpoint.plugin.app.interceptor;

import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.plugin.app.AppConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequest;

import static com.navercorp.pinpoint.plugin.app.util.AppHandlerUtils.getUsernameFromTraceContent;

/**
 * for org.springframework.http.client.support.HttpAccessor
 */
public class HttpAccessorCreateRequestMethodInterceptor implements AroundInterceptor {
    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());
    private final TraceContext traceContext;

    public HttpAccessorCreateRequestMethodInterceptor(TraceContext traceContext) {
        this.traceContext = traceContext;
    }

/*    public RequestBuilderBuildMethodInterceptor(TraceContext traceContext, MethodDescriptor methodDescriptor, InterceptorScope interceptorScope) {
    }*/

    @Override
    public void before(Object target, Object[] args) {
        logger.beforeInterceptor(target, args);
    }

    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {
        logger.afterInterceptor(target, args);
        logger.debug("{},{},{}",this,target,args);
        try {
            if (!(result instanceof ClientHttpRequest)) {
                return;
            }
            final ClientHttpRequest request = ((ClientHttpRequest) result);
            HttpHeaders header = request.getHeaders();
            if(null == header){
                return;
            }
            header.add(AppConstants.ACTION_KEY, getUsernameFromTraceContent(traceContext));
            return;
        } catch (Throwable t) {
            logger.warn("Failed to BEFORE process. {}", t.getMessage(), t);
        }
    }
}