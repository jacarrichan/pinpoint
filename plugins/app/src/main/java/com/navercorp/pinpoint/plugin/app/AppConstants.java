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
package com.navercorp.pinpoint.plugin.app;

import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.common.trace.ServiceTypeFactory;

import static com.navercorp.pinpoint.common.trace.ServiceTypeProperty.RECORD_STATISTICS;

/**
 * @author Jongho Moon
 * @author jaehong.kim
 *
 */
public final class AppConstants {
    public static final ServiceType APP = ServiceTypeFactory.of(2010, "APP", RECORD_STATISTICS);
    public static final ServiceType APP_METHOD = ServiceTypeFactory.of(2011, "APP_METHOD");
    private AppConstants() {
    }

    public static final String HTTP_CLIENT4_SCOPE = "HttpClient4Scope";
    public static final String HTTP_CLIENT3_METHOD_BASE_SCOPE = "HttpClient3MethodBase";

    public static final String ACTION_KEY = "teststars";

    public static final ServiceType OK_HTTP_CLIENT_INTERNAL = ServiceTypeFactory.of(9059, "OK_HTTP_CLIENT_INTERNAL", "OK_HTTP_CLIENT");

    public static final String SEND_REQUEST_SCOPE = "okHttpSendRequestScope";
}