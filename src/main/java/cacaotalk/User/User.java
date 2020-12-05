package cacaotalk.User;

import cacaotalk.JoinStatus.JoinStatus;
import cacaotalk.Message.Chat;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class User {

  final private static int MAX_USER_NAME_LEN = 24;
  final private static int MIN_USER_NAME_LEN = 1;

  final public static String kafkaHostname = "localhost:9092";
  final public static String stringDeserialize = "org.apache.kafka.common.serialization.StringDeserializer";
  final private static String initOffset = "earliest";

  private String userName;
  private String curJoinRoomName;
  private Producer producer;
  private Consumer consumer;
  private JoinStatusManager joinStatusManager;

  private User() {}

  private static class UserHolder {
    final private static User user = new User();
  }

  public static User getUser(){
    return UserHolder.user;
  }

  public void login(String userName){

    this.userName = userName;

    this.joinStatusManager = new JoinStatusManager(userName);

    this.consumer = new Consumer(userName);
    this.consumer.init(joinStatusManager.getJoinStatus());

    this.producer = new Producer(userName);

  }

  public void logout(){

    this.consumer.cleanUp();
    this.consumer = null;
    this.producer = null;
    this.userName = null;
    this.curJoinRoomName = null;
    this.joinStatusManager = null;

  }


  public static boolean isValidName(String name){
    if (name.length() >= MAX_USER_NAME_LEN){
      return false;
    }

    if (name.length() < MIN_USER_NAME_LEN){
      return false;
    }

    return name.matches("[A-Za-z0-9]+");
  }


  // As goToTop() saves offset '0' in server
  // If we do not save offsets after reading chats
  // Only offsets of '0' will be left in server
  // that will make user always be on top
  //
  public void readChats() {

    if (curJoinRoomName == null){
      System.err.println("readChats error\n");
      return;
    }

    HashMap<TopicPartition, Long> roomToOffset = this.consumer.readChats(
            curJoinRoomName
    );

    this.joinStatusManager.save(
            roomToOffset
    );
  }

  public void printJoiningRooms(){

    HashMap<String, JoinStatus> joinStatus = this.joinStatusManager.getJoinStatus();

    if (joinStatus == null || joinStatus.isEmpty()){
      System.out.println("현재 참여중인 채팅방이 없습니다.");
      return;
    }

    joinStatus.forEach(((roomName, j) -> {
      System.out.println(roomName);
    }));

  }

  public void register(String roomName){

    this.joinStatusManager.addNew(roomName);

    this.consumer.register(
            this.joinStatusManager.getJoinStatus()
    );

    System.out.println(
            "'" + roomName + "' is created!"
    );
  }

  public boolean didRegisterBefore(String roomName){

    return this.joinStatusManager.isJoining(roomName);
  }

  public void join(String roomName){

    this.curJoinRoomName = roomName;
  }

  public void goToTop(){

    if (curJoinRoomName == null){
      System.err.println("goToTop error\n");
      return;
    }

    TopicPartition resetRoom = this.consumer.resetCursor(curJoinRoomName);

    // Save Renewed offset
    HashMap<TopicPartition, Long> refreshedOffset = new HashMap<>();

    refreshedOffset.put(
            resetRoom,
            Long.valueOf(0)
    );

    this.joinStatusManager.save(
      refreshedOffset
    );
  }

  public void sendChat(String chatContent) {

    if (curJoinRoomName == null){
      System.err.println("sendChat error\n");
      return;
    }

    this.producer.sendChat(chatContent, curJoinRoomName);

  }


  public String getUserName(){
    return this.userName;
  }
}
