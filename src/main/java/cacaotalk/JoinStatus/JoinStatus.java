package cacaotalk.JoinStatus;

public class JoinStatus {

    private String roomName;
    private long offset;
    private long sendTime;

    public JoinStatus() {
    }

    public JoinStatus(String roomName, long offset) {
        this.roomName = roomName;
        this.offset = offset;
        this.sendTime = System.currentTimeMillis();
    }

    public String getRoomName(){
        return this.roomName;
    }

    public long getOffset(){
        return this.offset;
    }

    public long getSendTime(){
        return this.sendTime;
    }

    public void setRoomName(String roomName){
        this.roomName = roomName;
    }

    public void setOffset(long offset){
        this.offset = offset;
    }

    public void setSendTime(long time){
        this.sendTime = time;
    }

    public void refreshSendTime() {
        this.sendTime = System.currentTimeMillis();
    }

}