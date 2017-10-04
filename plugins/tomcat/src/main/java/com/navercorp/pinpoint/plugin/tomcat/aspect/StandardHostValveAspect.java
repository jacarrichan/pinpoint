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
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * filtering pinpoint header
 *
 * @author emeroad
 */
@Aspect
public abstract class StandardHostValveAspect {
    @PointCut
    public final void invoke(org.apache.catalina.connector.Request request, org.apache.catalina.connector.Response response) throws IOException, ServletException {
        System.err.println("StandardHostValveAspect");
        bindCookieThreadlocal(request, response);
        if (handFilter(request, response)) {
            String msg = "请求参数中有发现{},将不会进入正常的业务流程";
            System.err.println(msg);
            return;
        }
        __invoke(request, response);
    }

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


    @JointPoint
    abstract void __invoke(Request request, Response response) throws IOException, ServletException;
}