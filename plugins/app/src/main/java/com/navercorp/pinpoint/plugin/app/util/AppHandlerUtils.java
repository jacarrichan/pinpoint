package com.navercorp.pinpoint.plugin.app.util;

import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.plugin.app.AppConstants;


/**
 * Created by Jacarri on 2017/10/9.
 */
public final class AppHandlerUtils {
    private static final PLogger LOGGER = PLoggerFactory.getLogger(AppHandlerUtils.class);

    private static final String ACTION_KEY_SET_USERNAME = "su";
    private static final String ACTION_KEY_GET_USERNAME = "gu";
    private static final String USERNAME_KEY = "username";
    private static final String tip = "你能看到此信息是因为请求参数之中有" + AppConstants.ACTION_KEY + "\r\n";

    public static String getUsernameFromTraceContent(TraceContext traceContext) {
        String username = "serverhost";
        Trace trace = traceContext.currentTraceObject();
        if (null == trace) {
            LOGGER.trace("没有获取到trace，不能将username放在http的请求头中");
            return username;
        }
        username = trace.getTraceAlias();
        if (null == username) {
            LOGGER.trace("没有获取到username，不能将username放在http的请求头中");
            return "";
        }
        return username;
    }
}
