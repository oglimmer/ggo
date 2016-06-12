package de.oglimmer.ggo.com;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.config.managed.Decoder;

import javax.inject.Inject;
import java.io.IOException;

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
