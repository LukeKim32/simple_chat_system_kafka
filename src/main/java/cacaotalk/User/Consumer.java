package cacaotalk.User;

import cacaotalk.Engine.ChatSystem;
import cacaotalk.Engine.ChatSystemConfig;
import cacaotalk.JoinStatus.JoinStatus;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import cacaotalk.Message.Chat;

import java.time.Duration;
import java.util.*;


public class Consumer {


  private KafkaConsumer<String, Chat> consumer;

  public Consumer(String userName) {

    Properties properties = getProperties();

    properties.setProperty(
            ConsumerConfig.GROUP_ID_CONFIG,
            userName
    );

    properties.setProperty(
            ConsumerConfig.CLIENT_ID_CONFIG,
            userName
    );

    consumer = new KafkaConsumer<String, Chat>(
            properties
    );

  }

  // Subscribe Previous chatrooms
  public void init(HashMap<String, JoinStatus> joinStatus){

    if (joinStatus == null){
      return;
    }

    ArrayList<TopicPartition> joinedList = register(joinStatus);

    for (TopicPartition room : joinedList){
      this.consumer.seek(
              room,
              joinStatus.get(room.topic())
                      .getOffset()
      );
    }

  }


  // 새로 조인하면 레지스터가 불린다.
  // 이 때, 다른 방들의 오프셋도 초기화가 되는지를 확인해야한다.
  public ArrayList<TopicPartition> register(HashMap<String, JoinStatus> joinStatus){

    ArrayList<TopicPartition> joinedList = new ArrayList<>(
            joinStatus.size()
    );

    joinStatus.forEach(((roomName, j) -> {

      TopicPartition curRoom = new TopicPartition(
              roomName,
              0
      );

      joinedList.add(curRoom);
    }));

    consumer.assign(joinedList);

    return joinedList;
  }

  // 되는 테스트케이스 1. read를 한 뒤, (새로운 데이터 들어옴), 로그아웃 또는 껏다 키면, 새로운 데이터 부터 잘 읽힌다
  // 되는 테스트케이스 2. read를 다 한 뒤, 채팅방 맨 위로 갔다가, 더이상 읽지않고 바로 Chatroom에서 Chatting Window으로 Exit했다가, 다시 들어오면, 다시 맨 위부터 읽힌다 (맨 위가 유지)
  // 안되는 테스트케이스 1. Chatroom 맨 위로 갔다가 (더이상 읽지않고), 로그아웃 또는 프로그램을 재시작 하면, 이전에 설정했던 맨 위가 아닌, 채팅방 맨 위로 가기 전 가장 최근 오프셋 다음으로 설정이 된다.
  // 예 :  A send 데이터 2개 => B는 읽고, Chatroom 맨 위로 간 뒤에 로그아웃. --설명--> B가 다시 들어오면 읽었던 데이터 2개 이후 새 데이터가 없으므로 읽히는 게 없다 (맨 위가 유지 X)
  //                                       ㄴ 여기서 A가 데이터를 2개 더 보내놓고, B가 다시 들어와서 읽는다면? ->  새로운 2개를 읽는다 (처음부터가 아니라) (맨 위가 유지 X)
  //
  public TopicPartition resetCursor(String roomName){

    TopicPartition targetRoom = null;

    try {
      System.out.println("맨 위로");

      targetRoom = new TopicPartition(
              roomName,
              0
      );

      consumer.seekToBeginning(
              Collections.singleton(
                      targetRoom
              )
      );

      consumer.commitSync();

      return targetRoom;

    }catch(IllegalStateException e) {
      System.err.println("seek beginning error");
      System.err.println(e.getMessage());
    }

    return  targetRoom;
  }

  public void cleanUp() {
    consumer.unsubscribe();
  }

  private boolean isCurrentListening(String roomName, ConsumerRecord record){
    return roomName.equals((String)record.key());
  }

  public HashMap<TopicPartition, Long> readChats(String roomName) {

    HashMap<TopicPartition, Long> roomToOffset = null;

    try {

      TopicPartition curListeningRoom = new TopicPartition(
              roomName,
              0
      );

      Set<TopicPartition> roomNameSet = Collections.singleton(
              curListeningRoom
      );

      Map<TopicPartition, Long> offsetMap = consumer.endOffsets(
              roomNameSet
      );

      long endOffset = 0;

      if (!offsetMap.isEmpty()){
        Long endOffsetWrapper = (Long) offsetMap.values()
                                                .toArray()[0];

        endOffset = endOffsetWrapper.longValue();
      }


      long startOffset = consumer.position(
              curListeningRoom
      );

      roomToOffset = getRoomOffsetMap();

      while(startOffset < endOffset){

        ConsumerRecords<String, Chat> chatRecords = consumer.poll(
                Duration.ofMillis(100)
        );

        for(ConsumerRecord<String, Chat> chatRecord: chatRecords) {

          // Manually Filtering..
          //
          if (isCurrentListening(roomName, chatRecord)){

            Chat chat = chatRecord.value();

            System.out.println(
                    chat.toFormattedString()
            );

            startOffset += 1;

          }

        }
      }

      // Restore offsets after reading all except listening room
      roomToOffset.remove(curListeningRoom);

      restore(roomToOffset);

      roomToOffset.put(curListeningRoom, endOffset);

      return roomToOffset;

    }catch(Exception e){
      System.err.println(e.getMessage());
    }

    return roomToOffset;
  }

  private void restore(HashMap<TopicPartition, Long> roomToOffset){

    roomToOffset.forEach(((topicPartition, offset) -> {
        consumer.seek(topicPartition, offset.longValue());
    }));

    consumer.commitSync();
  }

  public HashMap<TopicPartition, Long> getRoomOffsetMap(){
    Set<TopicPartition> listeningRooms = consumer.assignment();

    HashMap<TopicPartition, Long> roomToOffset = new HashMap<>();

    // Collect Offsets before poll except for the one currently listening
    for (TopicPartition room : listeningRooms){
        long offset = consumer.position(room);
        roomToOffset.put(room, Long.valueOf(offset));
    }

    return roomToOffset;
  }

  private static Properties getProperties(){

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
            ChatSystemConfig.chatDeserialize
    );

    properties.setProperty(
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
            ChatSystemConfig.initOffset
    );

    properties.setProperty(
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
            Boolean.toString(ChatSystemConfig.AUTO_COMMIT)
    );

    return properties;
  }

  private void dummyPollForLazySubscription(){
    while (true){
      ConsumerRecords<String, Chat> chatRecords =  consumer.poll(
              Duration.ofMillis(100)
      );

      if (chatRecords.count() == 0) {
        break;
      }
    }
  }
}