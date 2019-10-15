package wifidirect.wifidirect.Message;

public interface IMessage {
    String getText();
    boolean isBelongsToCurrentUser();
    String getSenderName();
}
