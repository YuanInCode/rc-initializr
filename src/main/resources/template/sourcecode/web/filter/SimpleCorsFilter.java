package ${package}.web.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS;

/**
 * 处理跨域
 */
@Slf4j
public class SimpleCorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String origin = httpServletRequest.getHeader("Origin");
        Map<String, String> corsHeaders = getCorsHeaders(origin, httpServletRequest);
        corsHeaders.forEach(httpServletResponse::addHeader);

        chain.doFilter(request, response);
    }

    private Map<String, String> getCorsHeaders(String origin, HttpServletRequest httpServletRequest) {
        String allowHeaders = httpServletRequest.getHeader(ACCESS_CONTROL_REQUEST_HEADERS);
        if (!StringUtils.hasText(allowHeaders)) {
            allowHeaders = "ct,ut,activityId,activityName,uid,Content-Type";
        }
        if (!StringUtils.hasText(origin)) {
            origin = "*";
        }

        Map<String, String> corsHeaders = new HashMap<>();
        corsHeaders.put(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        corsHeaders.put(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, allowHeaders);
        corsHeaders.put(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*");
        corsHeaders.put(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        corsHeaders.put(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "*");
        corsHeaders.put(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
        return corsHeaders;
    }
}
