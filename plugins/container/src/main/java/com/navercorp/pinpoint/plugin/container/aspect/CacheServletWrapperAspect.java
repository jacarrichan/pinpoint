package com.navercorp.pinpoint.plugin.container.aspect;

import com.navercorp.pinpoint.bootstrap.instrument.aspect.Aspect;
import com.navercorp.pinpoint.bootstrap.instrument.aspect.JointPoint;
import com.navercorp.pinpoint.bootstrap.instrument.aspect.PointCut;
import com.navercorp.pinpoint.plugin.container.util.ServletHandlerUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * for websphere
 * Created by Jacarri on 2017-12-27.
 */
@Aspect
public abstract class CacheServletWrapperAspect {
    @PointCut
    void handleRequest(ServletRequest request, ServletResponse response)
            throws IOException {
        if (!ServletHandlerUtils.process((HttpServletRequest)request, (HttpServletResponse)response)) {
            __handleRequest(request, response);
        }
    }

    @JointPoint
    abstract void __handleRequest(ServletRequest req, ServletResponse res)
            throws IOException;
}
