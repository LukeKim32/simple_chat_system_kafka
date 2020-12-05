package cacaotalk.User;

import cacaotalk.Engine.ChatSystem;
import cacaotalk.Engine.ChatSystemConfig;
import cacaotalk.JoinStatus.JoinStatus;
import cacaotalk.Message.Chat;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.*;

public class JoinStatusManager {

  final private static String JOIN_STATUS_TOPIC = "joinStatus";

  private KafkaProducer<String, JoinStatus> recorder;
  private KafkaConsumer<String, JoinStatus> receiver;
  private HashMap<String, JoinStatus> joinStatusMap;
  private String userName;
  private String userJoinStatusTopic;


  public JoinStatusManager(String userName){

    this.userName = userName;
    this.userJoinStatusTopic = getUserJoinStatusName(userName);

    this.recorder = makeJoinRecorder();
    this.receiver = makeJoinReceiver(userName);

    this.joinStatusMap = receiveJoinStatus(userName);

  }

  public static String getUserJoinStatusName(String userName){
    return userName + "-" + JOIN_STATUS_TOPIC;
  }

  // join status topic 에다 저장
  // 중복된 room name 이 올 경우, 가장 최근 것으로 갱신
  public void save(HashMap<TopicPartition, Long> roomToOffset){

      roomToOffset.forEach(((topicPartition, offset) -> {

        try {

          JoinStatus prevJoinStatus = joinStatusMap.get(
                  topicPartition.topic()
          );

          prevJoinStatus.refreshSendTime();
          prevJoinStatus.setOffset(offset);

          RecordMetadata result = this.recorder.send(
                  new ProducerRecord<String, JoinStatus>(
                          this.userJoinStatusTopic,
                          this.userName,
                          prevJoinStatus
                  )
          ).get();

        } catch(Exception e){
          System.err.println("save error - " + e.getMessage());
        }

      }));
  }

  public void addNew(String roomName){

    try {

      JoinStatus newJoin = new JoinStatus(
              roomName,
              0
      );

      RecordMetadata result = this.recorder.send(
              new ProducerRecord<String, JoinStatus>(
                      this.userJoinStatusTopic,
                      this.userName,
                      newJoin
              )
      ).get();

      joinStatusMap.put(
              roomName,
              newJoin
      );

    } catch(Exception e){
      System.err.println("joinNew error - " + e.getMessage());
    }
  }

  public HashMap<String, JoinStatus> getJoinStatus(){
    return this.joinStatusMap;
  }

  public boolean isJoining(String roomName){
    return joinStatusMap.containsKey(roomName);
  }

  private HashMap<String, JoinStatus> receiveJoinStatus(String userName) {

    HashMap<String, JoinStatus> newJoinStatusMap = new HashMap<>();

    Set<TopicPartition> joinStatusTopic = Collections.singleton(
            new TopicPartition(this.userJoinStatusTopic, 0)
    );

    Map<TopicPartition, Long> offsetMap = receiver.endOffsets(
            joinStatusTopic
    );

    long endOffset = 0;

    if (!offsetMap.isEmpty()){
      Long endOffsetWrapper = (Long) offsetMap.values().toArray()[0];
      endOffset = endOffsetWrapper.longValue();
    }

    // Start reading from the start
    receiver.poll(Duration.ZERO);
    receiver.seekToBeginning(joinStatusTopic);

//    System.out.println("offset map : " + offsetMap);
//    System.out.println("전체 유저 참여 현황 받아오는지 확인하기 - 마지막 오프셋 : "+ endOffset);

    long startOffset = receiver.position(
            new TopicPartition(this.userJoinStatusTopic, 0)
    );

    while(startOffset < endOffset){

      ConsumerRecords<String, JoinStatus> allJoinRecords = receiver.poll(
              Duration.ofMillis(100)
      );

      for(ConsumerRecord<String, JoinStatus> joinRecord: allJoinRecords) {

        // Get User's
        if (joinRecord.key().equals(userName)){
          JoinStatus curJoinStatus = joinRecord.value();
          String roomName = curJoinStatus.getRoomName();
          long curSendTime = curJoinStatus.getSendTime();

          // If already exists
          if (newJoinStatusMap.containsKey(roomName)){
            JoinStatus prevJoinStatus = newJoinStatusMap.get(roomName);
            long prevSendTime = prevJoinStatus.getSendTime();

            if (prevSendTime < curSendTime){
              newJoinStatusMap.remove(roomName);
            }
          }

          newJoinStatusMap.put(
                  roomName,
                  curJoinStatus
          );
        }
      }

      startOffset += allJoinRecords.count();

    }

    return newJoinStatusMap;
  }

  private KafkaConsumer<String, JoinStatus> makeJoinReceiver(String userName){

    Properties properties = new Properties();

    properties.setProperty(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            ChatSystem.kafkaHostname
    );

    properties.setProperty(
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            ChatSystemConfig.stringDeserialize
    );

    properties.setProperty(
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            ChatSystemConfig.joinStatusDeserialize
    );

    properties.setProperty(
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
            ChatSystemConfig.initOffset
    );

    properties.setProperty(
            ConsumerConfig.GROUP_ID_CONFIG,
            userName
    );

    KafkaConsumer<String, JoinStatus> receiver = new KafkaConsumer<String, JoinStatus>(
            properties
    );

    receiver.assign(
            Collections.singleton(
                    new TopicPartition(this.userJoinStatusTopic, 0)
            )
    );

    return receiver;

  }

  private KafkaProducer<String, JoinStatus> makeJoinRecorder(){

    Properties properties = new Properties();

    properties.setProperty(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            ChatSystem.kafkaHostname
    );

    properties.setProperty(
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            ChatSystemConfig.stringSerialize
    );

    properties.setProperty(
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            ChatSystemConfig.joinStatusSerialize
    );

    return new KafkaProducer<String, JoinStatus>(properties);
  }
}
