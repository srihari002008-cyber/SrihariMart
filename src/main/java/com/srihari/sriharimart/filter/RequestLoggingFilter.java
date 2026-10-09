package com.srihari.sriharimart.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Filter generating a unique request ID for each HTTP request and attaching it to SLF4J MDC.
 * Implements Section 18 structured logging requirements.
 */
public class RequestLoggingFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final String MDC_KEY = "requestId";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String requestId = req.getHeader("X-Request-ID");
        if (requestId == null || requestId.trim().isEmpty()) {
            requestId = UUID.randomUUID().toString().substring(0, 8);
        }

        MDC.put(MDC_KEY, requestId);
        res.setHeader("X-Request-ID", requestId);

        long startTime = System.currentTimeMillis();
        try {
            LOGGER.debug("HTTP {} {} started", req.getMethod(), req.getRequestURI());
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            LOGGER.debug("HTTP {} {} completed in {} ms with status {}",
                    req.getMethod(), req.getRequestURI(), duration, res.getStatus());
            MDC.remove(MDC_KEY);
        }
    }

    @Override
    public void destroy() {
    }
}
