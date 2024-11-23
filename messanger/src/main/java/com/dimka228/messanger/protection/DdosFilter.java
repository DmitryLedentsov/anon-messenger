package com.dimka228.messanger.protection;

import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Order(1)
public class DdosFilter implements Filter {

    private final ConcurrentHashMap<String, AtomicLong> requestCount = new ConcurrentHashMap<>();
    private final long rateLimit = 70; 

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String ipAddress = request.getRemoteAddr();
        AtomicLong count = requestCount.computeIfAbsent(ipAddress, k -> new AtomicLong());

        if (count.incrementAndGet() > rateLimit) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(429 );
            //System.out.println("ddos attack!!!");
            httpResponse.getWriter().write("Rate limit exceeded. Please try again later.");
            return;
        }

        chain.doFilter(request, response);
    }

    /*
     * периодически сбрасываем инфу по запросам
     */
    @Scheduled(fixedRate = 20000)
	public void reportCurrentTime() {
		requestCount.clear();
	}

 
}