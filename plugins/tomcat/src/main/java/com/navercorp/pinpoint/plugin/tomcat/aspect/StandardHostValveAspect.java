/*
 * Copyright 2014 NAVER Corp.
 *
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

package com.navercorp.pinpoint.plugin.tomcat.aspect;

import com.navercorp.pinpoint.bootstrap.instrument.aspect.Aspect;
import com.navercorp.pinpoint.bootstrap.instrument.aspect.JointPoint;
import com.navercorp.pinpoint.bootstrap.instrument.aspect.PointCut;
import com.navercorp.pinpoint.plugin.tomcat.util.ServletHandletUtils;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * filtering pinpoint header
 *
 * @author emeroad
 */
@Aspect
public abstract class StandardHostValveAspect {
    @PointCut
    public final void invoke(org.apache.catalina.connector.Request request, org.apache.catalina.connector.Response response) throws IOException, ServletException {
        if (!ServletHandletUtils.process(request, response)) {
            __invoke(request, response);
        }
    }

    @JointPoint
    abstract void __invoke(Request request, Response response) throws IOException, ServletException;
}