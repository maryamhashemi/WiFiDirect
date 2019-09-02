package wifidirect.wifidirect.Chat;

import wifidirect.wifidirect.Chat.Device;

public interface IMsgReceived {
    void MsgReceived(Device device, String msg);
}

