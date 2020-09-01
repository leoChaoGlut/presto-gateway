package personal.leo.presto.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import personal.leo.presto.gateway.service.CoordinatorService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

@Slf4j
@Controller
public class DispatchController {
    @Autowired
    CoordinatorService coordinatorService;

    @Retryable(maxAttempts = 10, recover = "writeExceptionToCliResp")
    @PostMapping("/*/**")
    public void doPost(HttpServletRequest cliReq, HttpServletResponse cliResp) throws IOException {
        try (CloseableHttpClient proxyHttpClient = HttpClients.createDefault()) {
            final String coordinatorUrl = coordinatorService.fetchCoordinatorUrl();
            HttpPost proxyPost = new HttpPost(coordinatorUrl + cliReq.getRequestURI());

            final Enumeration<String> cliHeaderNames = cliReq.getHeaderNames();
            while (cliHeaderNames.hasMoreElements()) {
                final String cliHeaderName = cliHeaderNames.nextElement();
                if (HTTP.CONTENT_LEN.equalsIgnoreCase(cliHeaderName)) {
                    continue;
                }
                final String cliHeaderValue = cliReq.getHeader(cliHeaderName);
                proxyPost.setHeader(cliHeaderName, cliHeaderValue);
            }

            proxyPost.setEntity(new InputStreamEntity(cliReq.getInputStream(), ContentType.create("text/plain", StandardCharsets.UTF_8)));

            try (CloseableHttpResponse proxyResp = proxyHttpClient.execute(proxyPost)) {
                cliResp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
                proxyResp.getEntity().writeTo(cliResp.getOutputStream());
            }
        }
    }

    @Retryable(maxAttempts = 10, recover = "writeExceptionToCliResp")
    @GetMapping("/*/**")
    public void doGet(HttpServletRequest cliReq, HttpServletResponse cliResp) throws IOException {
        try (CloseableHttpClient proxyHttpClient = HttpClients.createDefault()) {
            final String coordinatorUrl = coordinatorService.fetchCoordinatorUrl();
            HttpGet proxyGet = new HttpGet(coordinatorUrl + cliReq.getRequestURI());

            final Enumeration<String> cliHeaderNames = cliReq.getHeaderNames();
            while (cliHeaderNames.hasMoreElements()) {
                final String cliHeaderName = cliHeaderNames.nextElement();
                final String cliHeaderValue = cliReq.getHeader(cliHeaderName);
                proxyGet.setHeader(cliHeaderName, cliHeaderValue);
            }

            try (CloseableHttpResponse proxyResp = proxyHttpClient.execute(proxyGet)) {
                cliResp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
                proxyResp.getEntity().writeTo(cliResp.getOutputStream());
            }
        }
    }

    @Recover
    public void writeExceptionToCliResp(Exception e, HttpServletRequest cliReq, HttpServletResponse cliResp) throws IOException {
        IOUtils.write(e.getMessage(), cliResp.getOutputStream(), StandardCharsets.UTF_8);
    }
}
