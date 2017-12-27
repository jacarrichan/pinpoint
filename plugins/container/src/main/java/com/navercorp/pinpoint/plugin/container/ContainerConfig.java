/*
 * Copyright 2016 NAVER Corp.
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
 *
 */

package com.navercorp.pinpoint.plugin.container;

import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;

import static com.navercorp.pinpoint.plugin.container.util.ConfigUtil.getUsernameFromConfig;

/**
 * @author Woonduk Kang(emeroad)
 */
public class ContainerConfig {
    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());
    private String username;

    public ContainerConfig(ProfilerConfig config) {
        this.username = getUsernameFromConfig(config);
    }

    public String getUsername() {
        return username;
    }
}
