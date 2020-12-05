package cacaotalk.Engine;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.producer.ProducerConfig;
import cacaotalk.Message.Chat;

import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;


public class ChatSystem {

    final public static String kafkaHostname = "localhost:9092";

    final private static int numPartitions = 1;
    final private static short replicaNum = 1;

    private static AdminClient client;

    private ChatSystem(){ }

    private static class ChatSystemHolder {
        final private static ChatSystem chatSystem = new ChatSystem();
    }

    public static ChatSystem getChatSystem(){
        return ChatSystemHolder.chatSystem;

    }

    public void init() {
        Properties adminProperties = getProperties();

        client = KafkaAdminClient.create(adminProperties);
    }

    public void createJoinStatusIfFirst(String joinStatusName){
        if (!isRoomExist(joinStatusName)){
            createRoom(joinStatusName);
        }
    }

    public Set<String> getRoomList() throws Exception {

        return client.listTopics().names().get();
    }

    public void printRooms() {
        try {
            Set<String> chatRooms = this.getRoomList();

            if (chatRooms.isEmpty()){
                System.out.println("채팅방이 없습니다.");
                return;
            }

            for (String chatRoom : chatRooms){
                System.out.println(chatRoom);
            }

        }catch(Exception e){
            System.err.println(e.getMessage());
        }

    }

    public boolean isRoomExist(String roomName) {

        try {
            Set<String> chatRooms = this.getRoomList();

            return chatRooms.contains(roomName);

        }catch(Exception e){
            System.err.println(e.getMessage());
            return false;
        }

    }

    public void createRoom(String roomName) {

        try {

            // 이미 생성된 채팅방(Topic)이라면 생성 중단
            if (this.isRoomExist(roomName)){
                return;
            }

            NewTopic newChatroom = new NewTopic(
                    roomName,
                    numPartitions,
                    replicaNum
            );


            client.createTopics(Collections.singleton(newChatroom))
                    .values()
                    .get(roomName);

        }catch (Exception e) {
//            System.out.println(roomName + " 생성 실패");
            System.out.print(e.getMessage());
        }
    }

    private static Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaHostname
        );

        properties.setProperty(
                AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG,
                "5000"
        );

        properties.setProperty(
                AdminClientConfig.RETRIES_CONFIG,
                "2"
        );

        return properties;
    }

}