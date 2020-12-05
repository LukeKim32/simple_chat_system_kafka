package cacaotalk.Message;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

public class ChatDeserializer implements Deserializer{

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map configs, boolean isKey) {

    }

    @Override
    public Object deserialize(String s, byte[] bytes) {

        try {
            return objectMapper.readValue(bytes, Chat.class);

        }catch(IOException e){
            System.err.println("deserialize error "+ e.getMessage());

        }

        return new byte[0];
    }

    @Override
    public Object deserialize(String topic, Headers headers, byte[] data) {

        try {
            return objectMapper.readValue(data, Chat.class);

        } catch(IOException e) {
            System.err.println("deserialize error - "+ e.getMessage());
        }

        return new byte[0];

    }

    @Override
    public void close() {

    }
}
