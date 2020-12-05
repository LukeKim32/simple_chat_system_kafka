package cacaotalk.Windows;

import cacaotalk.Engine.ChatSystem;
import cacaotalk.User.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// 채팅방이 중간에 사라지는 경우는 고려 X
public class ChatRoomWindow extends Window {

    final private static int READ = 1;
    final private static int WRITE = 2;
    final private static int RESET = 3;
    final private static int EXIT = 4;

    final private static String INPUT_CHAT_MSG = "Text : ";
    final private static String NOT_SUPPORTED_MODE_MSG = "지원하지 않는 모드입니다.";
    final private static String NUMERIC_INPUT_ERR_MSG = "숫자를 입력해주세요.";

    // Returns Username
    public static Result run(String roomName){

        ChatSystem chatSystem = ChatSystem.getChatSystem();

        if (!chatSystem.isRoomExist(roomName)){
            System.out.println(roomName + "채팅방이 없습니다.");
            return new Result(null, Type.CHATTING);
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader( System.in )
        );

        printStartMsg(roomName);

        User user;

        while(true){

            System.out.print(Window.CLI_TERMINAL);

            try {
                String input = reader.readLine();

                if (input.length() > 0){

                    int mode = Integer.parseInt(input);

                    switch (mode) {
                        case READ:

                            user = User.getUser();

                            user.readChats();

                            break;

                        case WRITE:

                            System.out.print(INPUT_CHAT_MSG);

                            String chatContent = reader.readLine();

                            user = User.getUser();

                            user.sendChat(chatContent);

                            break;

                        case RESET:

                            user = User.getUser();

                            user.goToTop();

                            break;

                        case EXIT:

                            return new Result(null, Type.CHATTING);

                        default :
                            System.out.println(NOT_SUPPORTED_MODE_MSG);

                    }
                }


            }catch(IOException e){
                System.err.println(e.getMessage());

            }catch(NumberFormatException e){
                System.out.println(NUMERIC_INPUT_ERR_MSG);
            }

            if (!chatSystem.isRoomExist(roomName)){
                System.out.println(roomName + "채팅방이 없습니다.");
                return new Result(null, Type.CHATTING);
            }
        }
    }

    private static void printStartMsg(String roomName){
        System.out.println(roomName);
        System.out.println("1. Read");
        System.out.println("2. Write");
        System.out.println("3. Reset");
        System.out.println("4. Exit");
    }
}
