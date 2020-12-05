package cacaotalk;

import cacaotalk.Engine.ChatSystem;
import cacaotalk.User.JoinStatusManager;
import cacaotalk.User.User;
import cacaotalk.Windows.ChatRoomWindow;
import cacaotalk.Windows.ChattingWindow;
import cacaotalk.Windows.LoginWindow;
import cacaotalk.Windows.Window;

public class Main {

    public static void main(String[] args) {

        Window.Result windowResult = null;
        int curWindow = Window.Type.LOG_IN;

        ChatSystem chatSystem = ChatSystem.getChatSystem();
        chatSystem.init();

        User user = User.getUser();

        while(true) {

            switch(curWindow){
                case Window.Type.LOG_IN:

                    windowResult = LoginWindow.run();

                    if (windowResult.nextWindow != Window.Type.EXIT){

                        String userName = (String) windowResult.value;

                        chatSystem = ChatSystem.getChatSystem();

                        chatSystem.createJoinStatusIfFirst(
                                JoinStatusManager.getUserJoinStatusName(userName)
                        );

                        user.login(userName);
                    }

                    break;

                case Window.Type.CHATTING:

                    windowResult = ChattingWindow.run();

                    break;

                case Window.Type.CHAT_ROOM:

                    String roomName = (String) windowResult.value;

                    windowResult = ChatRoomWindow.run(roomName);

                    break;

                case Window.Type.EXIT:
                    System.exit(1);
                    break;
            }

            curWindow = windowResult.nextWindow;

        }


    }
}
