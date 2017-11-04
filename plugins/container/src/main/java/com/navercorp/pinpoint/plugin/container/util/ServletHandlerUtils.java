package com.navercorp.pinpoint.plugin.container.util;

import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.plugin.container.ContainerConstants;
import com.process.ZoaThreadLocal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;


/**
 * Created by Jacarri on 2017/10/9.
 */
public final class ServletHandlerUtils {
    private static final PLogger LOGGER = PLoggerFactory.getLogger(ServletHandlerUtils.class);

    private static final String ACTION_KEY_SET_USERNAME = "su";
    private static final String ACTION_KEY_GET_USERNAME = "gu";
    private static final String USERNAME_KEY = "username";
    private static final String tip = "你能看到此信息是因为请求参数之中有" + ContainerConstants.ACTION_KEY + "\r\n";

    public static boolean process(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean result = handFilter(request, response);
        String msg = "请求参数中有发现{},将不会进入正常的业务流程";
        if (result) {
            LOGGER.trace(msg, ContainerConstants.ACTION_KEY);
        }
        return result;
    }

    /**
     * 从request中取出cookie或者特定的header
     *
     * @param servletRequest
     * @param servletResponse
     * @param traceContext
     * @throws IOException
     */
    public static void bindRequestThreadlocal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, TraceContext traceContext) throws IOException {
        bindHeaderThreadlocal(servletRequest, servletResponse, traceContext);
        bindCookieThreadlocal(servletRequest, servletResponse, traceContext);
    }

    /**
     * 针对httpclient的请求，把header里面的信息取出来放在threadlocal
     */
    private static void bindHeaderThreadlocal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, TraceContext traceContext) throws IOException {
        String username = servletRequest.getHeader(ContainerConstants.ACTION_KEY);
        if (null == username) {
            LOGGER.trace("没有读取到上层应用通过HTTP传过来的username信息");
            return;
        }
        LOGGER.debug("读取到上层应用通过HTTP header传过来的username信息:{}", username);
        bindMq(username, traceContext);
    }

    /**
     * 针对浏览器的请求把cookie里面的信息取出来放在threadlocal
     */
    private static void bindCookieThreadlocal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, TraceContext traceContext) throws IOException {
        String username;
        HttpServletRequest hsr = servletRequest;
        Cookie[] cookies = hsr.getCookies();
        if (null == cookies) {
            return;
        }
        for (int i = 0; i < cookies.length; i++) {
            Cookie cookie = cookies[i];
            if (ContainerConstants.ACTION_KEY.equalsIgnoreCase(cookie.getName())) {
                username = cookie.getValue();
                LOGGER.debug("find username from cookie:{}", username);
                bindMq(username, traceContext);
            }
        }
    }

    /**
     * 放入mq中
     *
     * @param username
     */
    private final static void bindMq(String username, TraceContext traceContext) {
        if (null != traceContext) {
            Trace trace = traceContext.currentTraceObject();
            if (null == trace) {
                trace = traceContext.newTraceObject();
            }
            trace.setTraceAlias(username);
        }
        ZoaThreadLocal.G_Ins().A_CInfByID(username);
        LOGGER.debug("put mq:[{}] ", username);
    }

    /**
     * 设置或者取出username
     *
     * @param servletRequest
     * @param servletResponse
     * @return
     * @throws IOException
     */
    public static boolean handFilter(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        String username = null;
        if (!"GET".equalsIgnoreCase(servletRequest.getMethod())) {
            LOGGER.trace("不是get，不能使用getParameter");
            return false;
        }
        // 提前servletRequest.getParameter()会导致解决乱码的setCharacterEncoding无效
        Map parameterMap = servletRequest.getParameterMap();
        if (null == parameterMap || !parameterMap.containsKey(ContainerConstants.ACTION_KEY)) {
            return false;
        }
        Object akObj = servletRequest.getParameter(ContainerConstants.ACTION_KEY);
        if (null == akObj || !(akObj instanceof String)) {
            return false;
        }
        String ak = (String) akObj;
        if (!ACTION_KEY_GET_USERNAME.equals(ak) && !ACTION_KEY_SET_USERNAME.equals(ak)) {
            LOGGER.debug("ak值是" + ak);
            return false;
        }
        servletResponse.setContentType("text/plain; charset=utf-8");
        PrintWriter writer = servletResponse.getWriter();
        writer.write(tip);
        if (ACTION_KEY_GET_USERNAME.equals(ak)) {
            Cookie[] cookies = ((HttpServletRequest) servletRequest).getCookies();
            if (null == cookies) {
                username = "";
            } else {
                for (int i = 0; i < cookies.length; i++) {
                    Cookie cookie = cookies[i];
                    if (ContainerConstants.ACTION_KEY.equalsIgnoreCase(cookie.getName())) {
                        username = cookie.getValue();
                    }
                }
            }
            writer.write("username:" + username);
            return true;
        }
        username = servletRequest.getParameter(USERNAME_KEY);
        if (null == username || 0 == username.length()) {
            writer.write(String.format("%s 为%s，必须设置请求参数%s的值", ContainerConstants.ACTION_KEY,
                    ACTION_KEY_SET_USERNAME, USERNAME_KEY));
            return true;
        }
        HttpServletResponse hsres = (HttpServletResponse) servletResponse;
        Cookie cookie = new Cookie(ContainerConstants.ACTION_KEY, username);
        hsres.addCookie(cookie);
        return true;
    }
}
