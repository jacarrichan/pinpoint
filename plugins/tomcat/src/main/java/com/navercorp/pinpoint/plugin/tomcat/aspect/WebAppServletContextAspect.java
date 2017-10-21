package com.navercorp.pinpoint.plugin.tomcat.aspect;

import com.navercorp.pinpoint.bootstrap.instrument.aspect.Aspect;
import com.navercorp.pinpoint.bootstrap.instrument.aspect.JointPoint;
import com.navercorp.pinpoint.bootstrap.instrument.aspect.PointCut;
import com.navercorp.pinpoint.plugin.tomcat.util.ServletHandletUtils;
import weblogic.servlet.internal.ServletRequestImpl;
import weblogic.servlet.internal.ServletResponseImpl;

import java.io.IOException;

/**
 * for weblogic
 * Created by Jacarri on 2017-10-21.
 */
@Aspect
public abstract class WebAppServletContextAspect {
    @PointCut
    void execute(ServletRequestImpl request, ServletResponseImpl response)
            throws IOException {
        if (!ServletHandletUtils.process(request, response)) {
            __execute(request, response);
        }
    }

    @JointPoint
    abstract void __execute(ServletRequestImpl paramServletRequestImpl, ServletResponseImpl paramServletResponseImpl)
            throws IOException;
}
