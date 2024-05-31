package com.jayway.msocr.traces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Log4j2
public class LoggingFilter extends OncePerRequestFilter {

    private static final String ENCODING_UTF8 = "UTF-8";
    private static final String AUTHORIZATION = "authorization";
    private final AtomicLong id = new AtomicLong(0);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long requestId = id.incrementAndGet();
        RequestWrapper requestWrapper = new RequestWrapper(requestId, request);
        ResponseWrapper responseWrapper = new ResponseWrapper(requestId, response);

        request.setCharacterEncoding(ENCODING_UTF8);
        response.setCharacterEncoding(ENCODING_UTF8);

        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            if (!request.getRequestURI().contains("swagger")) {
                long endTime = System.currentTimeMillis();
                logRequest(request, requestWrapper, startTime);
                logResponse(requestWrapper, responseWrapper, startTime, endTime);
            }
        }
    }

    private void logRequest(HttpServletRequest request, RequestWrapper requestWrapper, long startTime) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode logJson = objectMapper.createObjectNode();
            logJson.put("type", "request");
            logJson.put("requestId", requestWrapper.getId());
            logJson.put("URI", request.getRequestURL().toString());
            logJson.put("method", request.getMethod());
            logJson.put("startTime", startTime);

            ObjectNode headersNode = logJson.putObject("headers");
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String key = headerNames.nextElement();
                String value = request.getHeader(key);
                if (!AUTHORIZATION.equalsIgnoreCase(key)) {
                    headersNode.put(key, value);
                }
            }

            byte[] requestBodyBytes = requestWrapper.toByteArray();
            if (requestBodyBytes.length > 0) {
                try {
                    Object requestBody = objectMapper.readValue(requestBodyBytes, Object.class);
                    logJson.set("requestBody", objectMapper.valueToTree(requestBody));
                } catch (IOException e) {
                    log.warn("Error processing request body: ", e);
                    logJson.put("requestBody", "Error processing body");
                }
            } else {
                logJson.put("requestBody", "Empty");
            }

            log.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logJson));
        } catch (IOException e) {
            log.error("Error generating request log: ", e);
        }
    }

    private void logResponse(RequestWrapper requestWrapper, ResponseWrapper responseWrapper, long startTime, long endTime) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode logJson = objectMapper.createObjectNode();
            logJson.put("type", "response");
            logJson.put("requestId", requestWrapper.getId());
            logJson.put("statusCode", responseWrapper.getStatus());
            logJson.put("statusText", HttpStatus.valueOf(responseWrapper.getStatus()).getReasonPhrase());
            logJson.put("startTime", startTime);
            logJson.put("endTime", endTime);
            logJson.put("timeTaken", endTime - startTime);

            ObjectNode responseHeadersNode = logJson.putObject("responseHeaders");
            for (String key : responseWrapper.getHeaderNames()) {
                if (!AUTHORIZATION.equalsIgnoreCase(key)) {
                    responseHeadersNode.put(key, responseWrapper.getHeader(key));
                }
            }

            byte[] responseBodyBytes = responseWrapper.toByteArray();
            String responseBody = new String(responseBodyBytes, StandardCharsets.UTF_8);
            try {
                // Parse the response body to ensure it is valid JSON
                Object responseBodyJson = objectMapper.readValue(responseBody, Object.class);
                logJson.set("responseBody", objectMapper.valueToTree(responseBodyJson));
            } catch (IOException e) {
                log.warn("Error processing response body: ", e);
                logJson.put("responseBody", responseBody);  // Fallback to raw response body
            }

            log.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logJson));
        } catch (IOException e) {
            log.error("Error generating response log: ", e);
        }
    }
}