/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package com.bradmcevoy.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletResponse extends AbstractResponse {

    private static final Logger log = LoggerFactory.getLogger(ServletResponse.class);
    private static ThreadLocal<HttpServletResponse> tlResponse = new ThreadLocal<HttpServletResponse>();

    /**
     * We make this available via a threadlocal so it can be accessed from parts
     * of the application which don't have a reference to the servletresponse
     */
    public static HttpServletResponse getResponse() {
        return tlResponse.get();
    }
    private final HttpServletResponse r;
//    private ByteArrayOutputStream out = new ByteArrayOutputStream();
    private Response.Status status;
    private Map<String, String> headers = new HashMap<String, String>();

    public ServletResponse(HttpServletResponse r) {
        this.r = r;
        tlResponse.set(r);
    }

    /**
     * Override to use servlets own date setting
     *
     * @param name
     * @param date
     */
    @Override
    protected void setAnyDateHeader(Header name, Date date) {
        r.setDateHeader(name.code, date.getTime());
    }

    @Override
    public String getNonStandardHeader(String code) {
        return headers.get(code);
    }

    @Override
    public void setNonStandardHeader(String name, String value) {
        r.addHeader(name, value);
        headers.put(name, value);
    }

    @Override
    public void setStatus(Response.Status status) {
        if (status.text == null) {
            r.setStatus(status.code);
        } else {
            r.setStatus(status.code, status.text);
        }
        this.status = status;
    }

    @Override
    public Response.Status getStatus() {
        return status;
    }

    @Override
    public OutputStream getOutputStream() {
        try {
//        return out;
            return r.getOutputStream();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void close() {
        try {
            r.flushBuffer();
        } catch (Throwable ex) {
            log.trace("exception closing and flushing", ex);
        }
    }

    @Override
    public void sendRedirect(String url) {
        String u = r.encodeRedirectURL(url);
        try {
            r.sendRedirect(u);
        } catch (IOException ex) {
            log.warn("exception sending redirect", ex);
        }
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public void setAuthenticateHeader(List<String> challenges) {
        for (String ch : challenges) {
            r.addHeader(Response.Header.WWW_AUTHENTICATE.code, ch);
        }
    }

    public Cookie setCookie(Cookie cookie) {
        if (cookie instanceof ServletCookie) {
            ServletCookie sc = (ServletCookie) cookie;
            r.addCookie(sc.getWrappedCookie());
            return cookie;
        } else {
            javax.servlet.http.Cookie c = new javax.servlet.http.Cookie(cookie.getName(), cookie.getValue());
            c.setDomain(cookie.getDomain());
            c.setMaxAge(cookie.getExpiry());
            c.setPath(cookie.getPath());
            c.setSecure(cookie.getSecure());
            c.setVersion(cookie.getVersion());

            r.addCookie(c);
            return new ServletCookie(c);
        }
    }

    public Cookie setCookie(String name, String value) {
        javax.servlet.http.Cookie c = new javax.servlet.http.Cookie(name, value);
        c.setPath("/");
        r.addCookie(c);
        return new ServletCookie(c);
    }
}
