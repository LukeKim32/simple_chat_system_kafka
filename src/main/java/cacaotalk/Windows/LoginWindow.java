package cacaotalk.Windows;

import cacaotalk.User.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoginWindow extends Window {

  final private static int LOG_IN = 1;
  final private static int EXIT = 2;
  final private static String INPUT_ID_TERMINAL = " ID : ";
  final private static String INPUT_ID_ERR_MSG = "올바르지 않은 ID 형식입니다.";
  final private static String INPUT_ID_FORMAT_MSG = "문자 혹은 숫자로 1~23자로 구성해야합니다.";
  final private static String NOT_SUPPORTED_MODE_MSG = "지원하지 않는 모드입니다.";
  final private static String NUMERIC_INPUT_ERR_MSG = "숫자를 입력해주세요.";

  // Returns Username
  public static Result run(){

    BufferedReader reader = new BufferedReader(
            new InputStreamReader( System.in )
    );

    printStartMsg();

    boolean isLoginMode = false;

    while(true){

      System.out.print(Window.CLI_TERMINAL);

      if (isLoginMode) {
        System.out.print(INPUT_ID_TERMINAL);
      }

      try {
        String input = reader.readLine();

        if (isLoginMode){

          String userName = input;

          if (User.isValidName(userName)){
            return new Result(userName, Type.CHATTING);
          }

          System.out.println(INPUT_ID_ERR_MSG);
          System.out.println(INPUT_ID_FORMAT_MSG);
          isLoginMode = false;

        }else {

          if (input.length() > 0){

            int mode = Integer.parseInt(input);

            switch (mode) {
              case LOG_IN:
                isLoginMode = true;
                break;

              case EXIT :
                return new Result(null, Type.EXIT);

              default :
                System.out.println(NOT_SUPPORTED_MODE_MSG);

            }
          }
        }


      }catch(IOException e){
        System.err.println(e.getMessage());

      }catch(NumberFormatException e){
        System.out.println(NUMERIC_INPUT_ERR_MSG);
      }

    }
  }

  private static void printStartMsg(){
    System.out.println("Welcome to CacaoTalk");
    System.out.println("1. Log in");
    System.out.println("2. Exit");
  }
}
