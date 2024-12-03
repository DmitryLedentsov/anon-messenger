package com.dimka228.messenger.protection;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Order(1)
public class DdosFilter implements Filter {

  private final ConcurrentHashMap<String, AtomicLong> requestCount = new ConcurrentHashMap<>();
  private final long rateLimit = 70;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String ipAddress = request.getRemoteAddr();
    AtomicLong count = requestCount.computeIfAbsent(ipAddress, k -> new AtomicLong());

    if (count.incrementAndGet() > rateLimit) {
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      httpResponse.setStatus(429);
      log.info("ddos attack!!!");
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
