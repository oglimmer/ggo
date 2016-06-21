package de.oglimmer.ggo.com;



import java.io.IOException;

import javax.inject.Inject;

import org.atmosphere.config.managed.Encoder;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonEncoder implements Encoder<CommandMessage, String> {

    @Inject
    private ObjectMapper mapper;

    @Override
    public String encode(CommandMessage m) {
        try {
            return mapper.writeValueAsString(m);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
