package cacaotalk.Message;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Chat {

    final private static String dateFormat = "yyyy-MM-dd 'at' HH:mm:ss z";
    final private static SimpleDateFormat foramtter = new SimpleDateFormat(dateFormat);

    private String userName;
    private String content;
    private long sendTime;


    public Chat() {
    }

    public Chat(String userName, String content) {
        this.userName = userName;
        this.content = content;
        this.sendTime = System.currentTimeMillis();
    }

    public String getUserName(){
        return this.userName;
    }

    public String getContent(){
        return this.content;
    }

    public long getSendTime(){
        return this.sendTime;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setSendTime(long time){
        this.sendTime = time;
    }

    public String toFormattedString(){

        Date sendTime = new Date(this.sendTime);

        return this.userName
                + ": "
                + this.content
                + " - 보낸 시각 : "
                + foramtter.format(sendTime);
    }

}