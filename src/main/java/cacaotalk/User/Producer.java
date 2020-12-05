package cacaotalk.User;

import cacaotalk.Engine.ChatSystem;
import cacaotalk.Engine.ChatSystemConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import cacaotalk.Message.Chat;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;

public class Producer {

    private KafkaProducer<String, Chat> producer;
    private String producerName;

    public Producer(String name) {

        Properties properties = getProperties();

        this.producer = new KafkaProducer<String, Chat>(properties);
        this.producerName = name;
    }

    private static Properties getProperties(){

        // Set properties used to configure the producer
        Properties properties = new Properties();
        // Set the brokers (bootstrap servers)

        properties.setProperty(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                ChatSystem.kafkaHostname
        );

        // Set how to serialize key/value pairs
        properties.setProperty(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                ChatSystemConfig.stringSerialize
        );

        properties.setProperty(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                ChatSystemConfig.chatSerialize
        );

        return properties;
    }

    public void sendChat(String chatContent, String roomName) {

        try {

            // check valid room name

            Chat chat = new Chat(this.producerName, chatContent);

            RecordMetadata result = this.producer.send(
                    new ProducerRecord<String, Chat>(
                            roomName,
                            roomName,
                            chat
                    )
            ).get();

        } catch(Exception e){
            System.err.println("sendChat error - " + e.getMessage());
        }
    }

}