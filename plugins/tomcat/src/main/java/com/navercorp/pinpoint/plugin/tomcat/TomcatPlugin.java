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
package com.navercorp.pinpoint.plugin.tomcat;

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
import com.navercorp.pinpoint.bootstrap.resolver.ConditionProvider;

import java.security.ProtectionDomain;

public class TomcatPlugin implements ProfilerPlugin, TransformTemplateAware {

    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());

    private TransformTemplate transformTemplate;

    /*
     * (non-Javadoc)
     * 
     * @see com.navercorp.pinpoint.bootstrap.plugin.ProfilerPlugin#setUp(com.navercorp.pinpoint.bootstrap.plugin.ProfilerPluginSetupContext)
     */
    @Override
    public void setup(ProfilerPluginSetupContext context) {

        final TomcatConfig config = new TomcatConfig(context.getConfig());
        if (logger.isInfoEnabled()) {
            logger.info("TomcatPlugin config:{}", config);
        }
        if (!config.isTomcatEnable()) {
            logger.info("TomcatPlugin disabled");
            return;
        }

        TomcatDetector tomcatDetector = new TomcatDetector(config.getTomcatBootstrapMains());
        context.addApplicationTypeDetector(tomcatDetector);

        addStandardHostValveAspectEditor();
        addRequestFacadeEditor();
        addHttpClientEditor();
        if (shouldAddTransformers(config)) {
            logger.info("Adding Tomcat transformers");
        } else {
            logger.info("Not adding Tomcat transfomers");
        }
    }

    private void addHttpClientEditor() {
         logger.info(" adding httpclient interceptor");
        addHttpMethodBaseClass();
        addHttpRequestExecutorClass();
        addRequestBuilder();
    }

    private boolean shouldAddTransformers(TomcatConfig config) {
        // Transform if conditional check is disabled
        if (!config.isTomcatConditionalTransformEnable()) {
            return true;
        }
        // Only transform if it's a Tomcat application or SpringBoot application
        ConditionProvider conditionProvider = ConditionProvider.DEFAULT_CONDITION_PROVIDER;
        boolean isTomcatApplication = conditionProvider.checkMainClass(config.getTomcatBootstrapMains());
        boolean isSpringBootApplication = conditionProvider.checkMainClass(config.getSpringBootBootstrapMains());
        return isTomcatApplication || isSpringBootApplication;
    }

    private void addRequestFacadeEditor() {
        transformTemplate.transform("org.apache.catalina.connector.RequestFacade", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);
                if (target != null) {
                    target.weave("com.navercorp.pinpoint.plugin.tomcat.aspect.RequestFacadeAspect");
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
                    target.weave("com.navercorp.pinpoint.plugin.tomcat.aspect.StandardHostValveAspect");
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
                    target.weave("com.navercorp.pinpoint.plugin.tomcat.aspect.ServerHandlerAspect");
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
                    target.weave("com.navercorp.pinpoint.plugin.tomcat.aspect.Jetty8ServerHandlerAspect");
                    return target.toBytecode();
                }

                return null;
            }
        });
    }


    /**
     * http3
     */
    private void addHttpMethodBaseClass() {
        transformTemplate.transform("org.apache.commons.httpclient.HttpMethodBase", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);

                InstrumentMethod execute = target.getDeclaredMethod("execute", "org.apache.commons.httpclient.HttpState", "org.apache.commons.httpclient.HttpConnection");
                if (execute != null) {
                    logger.debug("[http3] Add HttpMethodBase interceptor.");
                    execute.addScopedInterceptor("com.navercorp.pinpoint.plugin.tomcat.interceptor.HttpMethodBaseExecuteMethodInterceptor", TomcatConstants.HTTP_CLIENT3_METHOD_BASE_SCOPE, ExecutionPolicy.ALWAYS);
                }
                return target.toBytecode();
            }
        });
    }

    /**
     * 针对http4
     */
    private void addHttpRequestExecutorClass() {
        transformTemplate.transform("org.apache.http.protocol.HttpRequestExecutor", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);

                InstrumentMethod execute = target.getDeclaredMethod("execute", "org.apache.http.HttpRequest", "org.apache.http.HttpClientConnection", "org.apache.http.protocol.HttpContext");
                if (execute != null) {
                    logger.debug("[http4] Add HttpRequestExecutor interceptor.");
                    execute.addScopedInterceptor("com.navercorp.pinpoint.plugin.tomcat.interceptor.HttpRequestExecutorExecuteMethodInterceptor", TomcatConstants.HTTP_CLIENT4_SCOPE, ExecutionPolicy.ALWAYS);
                }
                return target.toBytecode();
            }
        });
    }

    /**
     * for  okhttp
     */
    private void addRequestBuilder() {
        transformTemplate.transform("com.squareup.okhttp.Request$Builder", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);
                InstrumentMethod buildMethod = target.getDeclaredMethod("build");
                if (buildMethod != null) {
                    logger.debug("[OkHttp] Add Request.Builder.build interceptor.");
                    buildMethod.addScopedInterceptor("com.navercorp.pinpoint.plugin.tomcat.interceptor.RequestBuilderBuildMethodInterceptor", TomcatConstants.SEND_REQUEST_SCOPE, ExecutionPolicy.ALWAYS);
                }

                return target.toBytecode();
            }
        });
    }

    @Override
    public void setTransformTemplate(TransformTemplate transformTemplate) {
        this.transformTemplate = transformTemplate;
    }
}
