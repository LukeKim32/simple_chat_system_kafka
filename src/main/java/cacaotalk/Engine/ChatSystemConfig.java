package cacaotalk.Engine;

import cacaotalk.JoinStatus.JoinStatusDeserializer;
import cacaotalk.JoinStatus.JoinStatusSerializer;
import cacaotalk.Message.ChatDeserializer;
import cacaotalk.Message.ChatSerializer;

public class ChatSystemConfig {
  final public static String stringSerialize = "org.apache.kafka.common.serialization.StringSerializer";
  final public static String stringDeserialize = "org.apache.kafka.common.serialization.StringDeserializer";

  final public static String chatSerialize = ChatSerializer.class.getName();
  final public static String chatDeserialize = ChatDeserializer.class.getName();

  final public static String joinStatusSerialize = JoinStatusSerializer.class.getName();
  final public static String joinStatusDeserialize = JoinStatusDeserializer.class.getName();


  final public static String initOffset = "earliest";
  final public static Boolean AUTO_COMMIT = true;

}
