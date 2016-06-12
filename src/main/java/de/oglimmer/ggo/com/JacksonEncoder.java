package de.oglimmer.ggo.com;



import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.config.managed.Encoder;

import javax.inject.Inject;
import java.io.IOException;

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
