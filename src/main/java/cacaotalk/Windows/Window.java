package cacaotalk.Windows;

public class Window {

    public static class Result {
      public Object value;

      public int nextWindow;

      public Result(Object value, int next) {
          this.value = value;
          this.nextWindow = next;
      }
    }

    public static class Type {

        final public static int EXIT = 0;

        final public static int LOG_IN = 1;
        final public static int CHATTING = 2;
        final public static int CHAT_ROOM = 3;
    }

    final public static String CLI_TERMINAL = "cacaotalk> ";

}
