package de.oglimmer.ggo.util;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

import de.oglimmer.ggo.random.RandomString;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "")
public class GridGameOneProperties {

    private Smtp smtp = new Smtp();
    private String runtimePassword;
    private boolean emailDisabled;
    private App app = new App();

    @Getter
    @Setter
    public static class Smtp {
        private String host;
        private int port;
        private String user;
        private String password;
        private boolean ssl;
        private String from;

    }

    @Getter
    @Setter
    public static class App {
        private String domain;
        private String urlPath;
    }

}