package wifidirect.wifidirect.Message;

// This is class to handle the message which is interchanged between devices.
public class Message implements IMessage {
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

    @Override
    public String getText() {
        return text;
    }

    @Override
    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }

    @Override
    public String getSenderName(){return senderName;}
}
