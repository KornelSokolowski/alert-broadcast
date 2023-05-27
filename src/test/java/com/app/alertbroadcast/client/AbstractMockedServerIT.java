package com.app.alertbroadcast.client;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.mockserver.integration.ClientAndServer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.TestSocketUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

public abstract class AbstractMockedServerIT {
    protected static ClientAndServer mockserver;

    @BeforeAll
    static void setUpMockServer() {
        int availableTcpPort = TestSocketUtils.findAvailableTcpPort();
        System.setProperty("test.server.port", String.valueOf(availableTcpPort));
        mockserver = ClientAndServer.startClientAndServer(availableTcpPort);
    }

    @AfterAll
    static void shutDownMockServer() {
        mockserver.stop();
    }

    @AfterEach
    void resetMockServerExpections() {
        mockserver.reset();
    }

    @SneakyThrows
    protected String load(String path) {
        InputStream inputStream = Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(path),
                () -> "resource not found under path: " + path);
        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }

    private void prepareMockResponse(String path, String mockedResponse) {
        mockserver
                .when(
                        request()
                                .withMethod(HttpMethod.GET.name())
                                .withPath(path))
                .respond(
                        response()
                                .withStatusCode(HttpStatus.OK.value())
                                .withBody(json(mockedResponse)));
    }

    protected void prepareMockedResponseFromFile(String urlPath, String mockedResponseFilePath) {
        String mockedResponse = load(mockedResponseFilePath);
        prepareMockResponse(urlPath, mockedResponse);
    }

}
