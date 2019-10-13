package wifidirect.wifidirect.ChatAsync;

/**
 * We use this interface to dispaly messages in UI.
 * Maybe it will be a console application or Android application.
 */
public interface IMsgReceived {
    void MsgReceived(Device device, String msg);
}

