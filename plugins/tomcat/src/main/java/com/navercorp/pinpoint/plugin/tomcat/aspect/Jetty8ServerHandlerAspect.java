package com.navercorp.pinpoint.plugin.tomcat.aspect;

import com.navercorp.pinpoint.bootstrap.instrument.aspect.Aspect;
import com.navercorp.pinpoint.bootstrap.instrument.aspect.JointPoint;
import com.navercorp.pinpoint.bootstrap.instrument.aspect.PointCut;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Jacarri on 2017-10-02.
 */
@Aspect
public abstract class Jetty8ServerHandlerAspect {
    @PointCut
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        System.err.println("ServerHandlerAspect");
        bindCookieThreadlocal(request, response);
        if (handFilter(request, response)) {
            String msg = "请求参数中有发现{},将不会进入正常的业务流程";
            System.err.println(msg);
            response.getWriter().close();
            return;
        }else {
            __handle(target, baseRequest, request, response);
        }
    }

    @JointPoint
    public abstract void __handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException ;

    // 把cookie里面的信息取出来放在threadlocal
    private static void bindCookieThreadlocal(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        final String ACTION_KEY = "teststars";
        String username;
        HttpServletRequest hsr = servletRequest;
        Cookie[] cookies = hsr.getCookies();
        for (int i = 0; i < cookies.length; i++) {
            Cookie cookie = cookies[i];
            if (ACTION_KEY.equalsIgnoreCase(cookie.getName())) {
                username = cookie.getValue();
                System.err.println("find username from cookie:"+username);
//                connThreadLocal.set(username);
            }
        }
    }

    private static boolean handFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException {
        final String ACTION_KEY = "teststars";
        final String ACTION_KEY_SET_USERNAME = "su";
        final String ACTION_KEY_GET_USERNAME = "gu";
        final String USERNAME_KEY = "username";
        final String tip = "你能看到此信息是因为请求参数之中有" + ACTION_KEY + "\r\n";
        String username = null;
        Object akObj = servletRequest.getParameter(ACTION_KEY);
        if (null == akObj || !(akObj instanceof String)) {
            return false;
        }
        String ak = (String) akObj;
        if (!ACTION_KEY_GET_USERNAME.equals(ak) && !ACTION_KEY_SET_USERNAME.equals(ak)) {
            System.err.println("ak值是" + ak);
            return false;
        }
        servletResponse.setContentType("text/plain; charset=utf-8");
        PrintWriter writer = servletResponse.getWriter();
        writer.write(tip);
        if (ACTION_KEY_GET_USERNAME.equals(ak)) {
            Cookie[] cookies = ((HttpServletRequest) servletRequest).getCookies();
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if (ACTION_KEY.equalsIgnoreCase(cookie.getName())) {
                    username = cookie.getValue();
                }
            }
            writer.write("username:" + username);
            return true;
        }
        username = servletRequest.getParameter(USERNAME_KEY);
        if (null == username || 0 == username.length()) {
            writer.write(String.format("%s 为%s，必须设置请求参数%s的值", ACTION_KEY,
                    ACTION_KEY_SET_USERNAME, USERNAME_KEY));
            return true;
        }
//        connThreadLocal.set(username);
        HttpServletResponse hsres = (HttpServletResponse) servletResponse;
        Cookie cookie = new Cookie(ACTION_KEY, username);
        hsres.addCookie(cookie);
        return true;
    }



}
