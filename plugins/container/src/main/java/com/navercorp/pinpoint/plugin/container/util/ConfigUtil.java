package com.navercorp.pinpoint.plugin.container.util;

import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;

/**
 * Created by jianglei on 2017/12/27.
 */
public abstract class ConfigUtil {
    private static final PLogger logger = PLoggerFactory.getLogger(ConfigUtil.class);

    public static String getUsernameFromConfig(ProfilerConfig config) {
        final String username = config.readString("profiler.container.username", "");
        logger.debug("load usename from config : {}", username);
        if (username.length() > 8) {
            throw new RuntimeException("profiler.container.username's length less than 8,cuurrent length is " + username.length());
        }
        return username;
    }
}
