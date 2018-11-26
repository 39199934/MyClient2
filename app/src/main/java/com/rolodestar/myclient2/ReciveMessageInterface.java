package com.rolodestar.myclient2;

public interface ReciveMessageInterface {

    public void onReciveTextMessage(byte[] msg);
//    public void onReciveJsonMessage()
    public void onReciveJsonMessage(byte[] msg);
}
