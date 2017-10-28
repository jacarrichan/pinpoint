/*
 * Copyright 2014 NAVER Corp.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.plugin.container;

import com.navercorp.pinpoint.bootstrap.instrument.InstrumentClass;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentException;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentMethod;
import com.navercorp.pinpoint.bootstrap.instrument.Instrumentor;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformCallback;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplate;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplateAware;
import com.navercorp.pinpoint.bootstrap.interceptor.scope.ExecutionPolicy;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPlugin;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPluginSetupContext;

import java.security.ProtectionDomain;

public class ContainerPlugin implements ProfilerPlugin, TransformTemplateAware {

    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());

    private TransformTemplate transformTemplate;

    /*
     * (non-Javadoc)
     * 
     * @see com.navercorp.pinpoint.bootstrap.plugin.ProfilerPlugin#setUp(com.navercorp.pinpoint.bootstrap.plugin.ProfilerPluginSetupContext)
     */
    @Override
    public void setup(ProfilerPluginSetupContext context) {
        logger.debug("Load Container plugin......");
        addStandardHostValveAspectEditor();
        addRequestFacadeEditor();
    }

    private void addRequestFacadeEditor() {
        transformTemplate.transform("org.apache.catalina.connector.RequestFacade", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);
                if (target != null) {
                    target.weave("com.navercorp.pinpoint.plugin.container.aspect.RequestFacadeAspect");
                    return target.toBytecode();
                }

                return null;
            }
        });
    }

    private void addStandardHostValveAspectEditor() {
        transformTemplate.transform("org.apache.catalina.core.StandardHostValve", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);
                if (target != null) {
                    //把cookie写到Threadlocal
                    InstrumentMethod execute = target.getDeclaredMethod("invoke", "org.apache.catalina.connector.Request", "org.apache.catalina.connector.Response");
                    if (execute != null) {
                        logger.debug("[tomcat] Add StandardHostValve interceptor.");
                        execute.addScopedInterceptor("com.navercorp.pinpoint.plugin.container.interceptor.StandardHostValveInterceptor", ContainerConstants.STANDARD_HOST_VALVE_INTERCEPTOR_SCOPE, ExecutionPolicy.ALWAYS);
                    }
                    //把request username写到cookie
                    target.weave("com.navercorp.pinpoint.plugin.container.aspect.StandardHostValveAspect");
                    return target.toBytecode();
                }

                return null;
            }
        });
        transformTemplate.transform("org.mortbay.jetty.handler.HandlerWrapper", new TransformCallback() {
            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);
                if (target != null) {
                    //把cookie写到Threadlocal
                    InstrumentMethod execute = target.getDeclaredMethod("handle", "javax.servlet.http.HttpServletRequest", "javax.servlet.http.HttpServletResponse","int");
                    if (execute != null) {
                        logger.debug("[tomcat] Add StandardHostValve interceptor.");
                        execute.addScopedInterceptor("com.navercorp.pinpoint.plugin.container.interceptor.ServerHandlerInterceptor", ContainerConstants.STANDARD_HOST_VALVE_INTERCEPTOR_SCOPE, ExecutionPolicy.ALWAYS);
                    }
                    //把request username写到cookie
                    target.weave("com.navercorp.pinpoint.plugin.container.aspect.ServerHandlerAspect");
                    return target.toBytecode();
                }

                return null;
            }
        });
        transformTemplate.transform("org.eclipse.jetty.server.handler.HandlerWrapper", new TransformCallback() {
            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);
                if (target != null) {
                    //把cookie写到Threadlocal
                    InstrumentMethod execute = target.getDeclaredMethod("handle", "java.lang.String","org.eclipse.jetty.server.Request", "javax.servlet.http.HttpServletRequest","javax.servlet.http.HttpServletResponse");
                    if (execute != null) {
                        logger.debug("[jetty] Add Jetty8ServerHandlerInterceptor interceptor.");
                        execute.addScopedInterceptor("com.navercorp.pinpoint.plugin.container.interceptor.Jetty8ServerHandlerInterceptor", ContainerConstants.STANDARD_HOST_VALVE_INTERCEPTOR_SCOPE, ExecutionPolicy.ALWAYS);
                    }
                    //把request username写到cookie
                    target.weave("com.navercorp.pinpoint.plugin.container.aspect.Jetty8ServerHandlerAspect");
                    return target.toBytecode();
                }

                return null;
            }
        });
        transformTemplate.transform("io.undertow.servlet.handlers.ServletDispatchingHandler", new TransformCallback() {
            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);
                if (target != null) {
                    //把cookie写到Threadlocal
                    InstrumentMethod execute = target.getDeclaredMethod("handleRequest", "io.undertow.server.HttpServerExchange");
                    if (execute != null) {
                        logger.debug("[undertow] Add ServletHandlerInterceptor interceptor.");
                        execute.addScopedInterceptor("com.navercorp.pinpoint.plugin.container.interceptor.ServletHandlerInterceptor", ContainerConstants.STANDARD_HOST_VALVE_INTERCEPTOR_SCOPE, ExecutionPolicy.ALWAYS);
                    }
                    //把request username写到cookie
                    target.weave("com.navercorp.pinpoint.plugin.container.aspect.ServletHandlerAspect");
                    return target.toBytecode();
                }

                return null;
            }
        });
        transformTemplate.transform("weblogic.servlet.internal.WebAppServletContext", new TransformCallback() {
            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);
                if (target != null) {
                    //把cookie写到Threadlocal
                    InstrumentMethod execute = target.getDeclaredMethod("execute", "weblogic.servlet.internal.ServletRequestImpl", "weblogic.servlet.internal.ServletResponseImpl");
                    if (execute != null) {
                        logger.debug("[weblogic] Add WebAppServletContextInterceptor interceptor.");
                        execute.addScopedInterceptor("com.navercorp.pinpoint.plugin.container.interceptor.WebAppServletContextInterceptor", ContainerConstants.STANDARD_HOST_VALVE_INTERCEPTOR_SCOPE, ExecutionPolicy.ALWAYS);
                    }
                    //把request username写到cookie
                    target.weave("com.navercorp.pinpoint.plugin.container.aspect.WebAppServletContextAspect");
                    return target.toBytecode();
                }

                return null;
            }
        });
    }

    @Override
    public void setTransformTemplate(TransformTemplate transformTemplate) {
        this.transformTemplate = transformTemplate;
    }
}
