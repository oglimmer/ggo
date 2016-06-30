package de.oglimmer.atmospheremvc.com;

import java.io.IOException;

import javax.inject.Inject;

import org.atmosphere.config.managed.Decoder;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonDecoder implements Decoder<String, CommandMessage> {

    @Inject
    private ObjectMapper mapper;

    @Override
    public CommandMessage decode(String s) {
        try {
            return mapper.readValue(s, CommandMessage.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
