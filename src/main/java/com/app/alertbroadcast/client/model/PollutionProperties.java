package com.app.alertbroadcast.client.model;

import com.app.alertbroadcast.service.YamlPropertySourceFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@RequiredArgsConstructor
@Configuration
@PropertySource(value = "classpath:pollution.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "pollution-properties")
public class PollutionProperties {

    @Getter
    private final List<Pollution> pollution;

    @Getter
    @Setter
    public static class Pollution {
        private String name;
        private int fair;
        private int moderate;
        private int poor;
        private int veryPoor;
        private int extremelyPoor;
    }
}
