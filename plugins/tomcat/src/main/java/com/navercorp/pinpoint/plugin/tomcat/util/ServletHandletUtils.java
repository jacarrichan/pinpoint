package com.navercorp.pinpoint.plugin.tomcat.util;

import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.plugin.tomcat.TomcatConstants;
import com.process.ZoaExp;
import com.process.ZoaString;
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
public final class ServletHandletUtils {
    private static final PLogger LOGGER = PLoggerFactory.getLogger(ServletHandletUtils.class);

    private static final String ACTION_KEY_SET_USERNAME = "su";
    private static final String ACTION_KEY_GET_USERNAME = "gu";
    private static final String USERNAME_KEY = "username";
    private static final String tip = "你能看到此信息是因为请求参数之中有" + TomcatConstants.ACTION_KEY + "\r\n";

    public static boolean process(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletHandletUtils.prepareLoadClass();
        ServletHandletUtils.bindHeaderThreadlocal(request, response);
        boolean result = ServletHandletUtils.handFilter(request, response);
        String msg = "请求参数中有发现{},将不会进入正常的业务流程";
        if (result) {
            LOGGER.debug(msg, TomcatConstants.ACTION_KEY);
        }
        return result;
    }


    /**
     * 针对httpclient的请求，把header里面的信息取出来放在threadlocal
     */
    public static void bindHeaderThreadlocal(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        String username = servletRequest.getHeader(TomcatConstants.ACTION_KEY);
        if (null == username) {
            LOGGER.debug("没有读取到上层应用通过HTTP传过来的username信息");
            return;
        }
            LOGGER.debug("读取到上层应用通过HTTP传过来的username信息:{}",username);
        bindMq(username);
    }

    /**
     * 针对浏览器的请求把cookie里面的信息取出来放在threadlocal
     */
    public static void bindCookieThreadlocal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, TraceContext traceContext) throws IOException {
        String username;
        HttpServletRequest hsr = servletRequest;
        Cookie[] cookies = hsr.getCookies();
        if (null == cookies) {
            return;
        }
        for (int i = 0; i < cookies.length; i++) {
            Cookie cookie = cookies[i];
            if (TomcatConstants.ACTION_KEY.equalsIgnoreCase(cookie.getName())) {
                username = cookie.getValue();
                LOGGER.debug("find username from cookie:{}", username);
                bindMq(username,traceContext);
            }
        }
    }

    /**
     * 放入mq中
     *
     * @param username
     */
    private final static void bindMq(String username) {
        bindMq(username,null);
    }
    private final static void bindMq(String username, TraceContext traceContext) {
        if(null != traceContext){
            Trace trace = traceContext.currentTraceObject();
            if(null == trace ){
                trace = traceContext.newTraceObject();
            }
            trace.setTraceAlias(username);
        }
        final String mqClassName = "com.process.ZoaThreadLocal";
        try {
            ZoaThreadLocal.G_Ins().A_CInfByID(username);
            Class cls = Class.forName(mqClassName);
            if (null == cls) {
                LOGGER.error("can't find {}", mqClassName);
                return;
            }
        } catch (Exception e) {
            LOGGER.error("error: {}", e.toString());
        }
        LOGGER.debug("put mq:[{}] ",username);
    }

    public static boolean handFilter(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        String username = null;
        if(!"GET".equalsIgnoreCase(servletRequest.getMethod())){
            LOGGER.debug("不是get，不能使用getParameter");
            return false;
        }
        // 提前servletRequest.getParameter()会导致解决乱码的setCharacterEncoding无效
        Map parameterMap = servletRequest.getParameterMap();
        if (null == parameterMap || !parameterMap.containsKey(TomcatConstants.ACTION_KEY)) {
            return false;
        }
        Object akObj = servletRequest.getParameter(TomcatConstants.ACTION_KEY);
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
                    if (TomcatConstants.ACTION_KEY.equalsIgnoreCase(cookie.getName())) {
                        username = cookie.getValue();
                        bindMq(username);
                    }
                }
            }
            writer.write("username:" + username);
            return true;
        }
        username = servletRequest.getParameter(USERNAME_KEY);
        if (null == username || 0 == username.length()) {
            writer.write(String.format("%s 为%s，必须设置请求参数%s的值", TomcatConstants.ACTION_KEY,
                    ACTION_KEY_SET_USERNAME, USERNAME_KEY));
            return true;
        }
        HttpServletResponse hsres = (HttpServletResponse) servletResponse;
        Cookie cookie = new Cookie(TomcatConstants.ACTION_KEY, username);
        hsres.addCookie(cookie);
        return true;
    }

    public static void prepareLoadClass() {
        Class<ZoaExp> class1 = ZoaExp.class;
        ZoaString zoa0 = new ZoaString();
        LOGGER.debug("ZoaExp classloader:{}", class1.getClassLoader());
        LOGGER.debug("ZoaString classloader:{}", zoa0.getClass().getClassLoader());
    }

    public static String getUsernameFromTraceContent(TraceContext traceContext){
        String username = "serverhost" ;
        Trace trace = traceContext.currentTraceObject();
        if(null == trace){
            LOGGER.debug("没有获取到trace，不能将username放在http的请求头中");
            return  username;
        }
        username = trace.getTraceAlias();
        if(null == username){
            LOGGER.debug("没有获取到username，不能将username放在http的请求头中");
            return "";
        }
        return username;
    }
}
