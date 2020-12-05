package cacaotalk.JoinStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;
import java.util.Map;

public class JoinStatusSerializer implements Serializer{

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String s, Object o) {

        try {

            return objectMapper.writeValueAsBytes(o);

        }catch(IOException e){
            System.err.println(e.getMessage());
        }

        return new byte[0];
    }

    @Override
    public byte[] serialize(String topic, Headers headers, Object data) {

        try {
            return objectMapper.writeValueAsBytes(data);

        }catch(JsonProcessingException e){
            System.err.println("serializer - 1 error "+ e);
        }

        return new byte[0];
    }

    @Override
    public void close() {

    }
}
