package cacaotalk.Windows;

import cacaotalk.Engine.ChatSystem;
import cacaotalk.User.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChattingWindow extends Window {

    final private static int LIST_CHAT = 1;
    final private static int MAKE_CHAT = 2;
    final private static int JOIN_CHAT = 3;
    final private static int LOG_OUT = 4;

    final private static String NOT_SUPPORTED_MODE_MSG = "지원하지 않는 모드입니다.";
    final private static String NUMERIC_INPUT_ERR_MSG = "숫자를 입력해주세요.";

    // Returns Username
    public static Result run(){

        BufferedReader reader = new BufferedReader(
                new InputStreamReader( System.in )
        );

        ChatSystem chatSystem;

        User user = User.getUser();
        String userName = user.getUserName();

        printStartMsg(userName);


        while(true){

            System.out.print(Window.CLI_TERMINAL);

            try {
                String input = reader.readLine();

                if (input.length() > 0){

                    int mode = Integer.parseInt(input);

                    switch (mode) {
                        case LIST_CHAT:

                            user = User.getUser();

                            user.printJoiningRooms();

                            break;

                        case MAKE_CHAT:

                            chatSystem = ChatSystem.getChatSystem();

                            System.out.print("Chat room name (공백은 '_'로 치환 적용): ");

                            String newRoomName = reader.readLine();

                            if (newRoomName.isEmpty() || newRoomName.isBlank()){
                                System.out.println("최소 1글자 이상 글자이어야합니다.");
                                break;
                            }

                            newRoomName = newRoomName.replace(' ', '_');

                            if (user.didRegisterBefore(newRoomName)){
                                System.out.println("동일한 이름의 채팅방이 이미 있습니다.");
                                break;
                            }

                            chatSystem.createRoom(newRoomName);

                            user.register(newRoomName);

                            break;

                        case JOIN_CHAT:

                            System.out.print("Chat room name : ");

                            String roomNameToJoin = reader.readLine();


                            user = User.getUser();

                            // Make 하지 않은 채팅방 접속을 막는다.
                            if (!user.didRegisterBefore(roomNameToJoin)){
                                System.out.println(roomNameToJoin + "은 존재하지 않는 채팅방입니다");
                                break;
                            }

                            // 실험 : 없는 채팅방 subscribe 하면 어떻게 되는지 확인해야함

                            user.join(roomNameToJoin);

                            return new Result(roomNameToJoin, Type.CHAT_ROOM);

                        case LOG_OUT:

                            user = User.getUser();

                            user.logout();

                            return new Result(null, Type.LOG_IN);

                        default :
                            System.out.println(NOT_SUPPORTED_MODE_MSG);

                    }
                }


            }catch(IOException e){
                System.err.println(e.getMessage());

            }catch(NumberFormatException e){
                System.out.println(NUMERIC_INPUT_ERR_MSG);
            }
        }
    }

    private static void printStartMsg(String userName){
        System.out.println(userName + "'s Chatting");
        System.out.println("1. List");
        System.out.println("2. Make");
        System.out.println("3. Join");
        System.out.println("4. Log out");
    }
}
