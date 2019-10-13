package wifidirect.wifidirect;

public class Message {
    private String text; // message body
    private boolean belongsToCurrentUser; // is this message sent by us?
    private String senderName;

    public Message(String text, boolean belongsToCurrentUser) {
        this.text = text;
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    public Message(String text, boolean belongsToCurrentUser, String senderName) {
        this.text = text;
        this.belongsToCurrentUser = belongsToCurrentUser;
        this.senderName = senderName;
    }

    public String getText() {
        return text;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }

    public String getSenderName(){return senderName;}
}
