package com.rolodestar.myclient2;


import android.nfc.Tag;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SocketModel  {
    ApplicationUtil applicationUtil=null;
   // InputStream inputStream=null;
    boolean isRunning;
    String clientName;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    ProcessMessage processMessage;
    String charSet;
    boolean historyMessageIsRead;

    public boolean isHistoryMessageIsRead() {
        return historyMessageIsRead;
    }

    public void setHistoryMessageIsRead(boolean historyMessageIsRead) {
        this.historyMessageIsRead = historyMessageIsRead;
    }

    public String getCharSet() {
        return charSet;
    }

    public void Charset(String charSet) {
        this.charSet = charSet;
    }

    public void setReciveMessageInterface(ReciveMessageInterface reciveMessageInterface) {
        this.reciveMessageInterface = reciveMessageInterface;
    }

    ReciveMessageInterface reciveMessageInterface=null;


    List<MessageStruct> historyMessage;

    class MessageStruct{
        byte[] msg;
        Date messageDate;
        boolean isReceive;

        public boolean getIsReceive() {
            return isReceive;
        }

        public void setIsReceive(boolean receive) {
            isReceive = receive;
        }

        public byte[] getMsg() {
            return msg;
        }

        public void setMsg(byte[] msg) {
            this.msg = msg;
        }

        public Date getMessageDate() {
            return messageDate;
        }

        public void setMessageDate(Date messageDate) {
            this.messageDate = messageDate;
        }
    }
    public SocketModel(ApplicationUtil applicationUtil){
        this.applicationUtil=applicationUtil;
        historyMessage=new ArrayList<MessageStruct>() ;
       // inputStream = applicationUtil.getInputStream();
        isRunning=true;
        charSet="UTF-8";
        clientName="client-ro";
        processMessage=new ProcessMessage();
        historyMessageIsRead=false;

        new ReadMessageThread().start();
    }

    @Override
    protected void finalize() throws Throwable {

        super.finalize();
    }
    void sendMessage(String msg)
    {
        try {
            byte[] waitForSendMsg=msg.getBytes(charSet);
            applicationUtil.sendMessageToHost(waitForSendMsg);
            MessageStruct messageStruct=new MessageStruct();
            messageStruct.setIsReceive(false);
            messageStruct.setMessageDate(new Date());
            messageStruct.setMsg(waitForSendMsg);
            historyMessage.add(messageStruct);
            setHistoryMessageIsRead(false);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    void sendAndBuildJsonMessage(String msg)
    {
        try {
            byte[] waitForSendMsg=msg.getBytes(charSet);

            MessageJson messageJson=new MessageJson();
            messageJson.setMsg(msg);
            messageJson.setType("message");
            messageJson.setHead("");
            messageJson.setTo("server");
            messageJson.setFrom(clientName);
            Gson gson=new Gson();
            String jsonMsg = gson.toJson(messageJson);

            applicationUtil.sendMessageToHost(jsonMsg.getBytes(charSet));

            MessageStruct messageStruct=new MessageStruct();
            messageStruct.setIsReceive(false);
            messageStruct.setMessageDate(new Date());
            messageStruct.setMsg(jsonMsg.getBytes(charSet));

            historyMessage.add(messageStruct);
            setHistoryMessageIsRead(false);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    class ReadMessageThread extends Thread
    {
        @Override
        public void run() {
            super.run();
            while(isRunning)
            {
                try {
                    if (applicationUtil.getInputStream() == null) {

                        //continue;
                    } else {
                        if (applicationUtil.getInputStream().available() > 0) {
                            try {
                                int size = applicationUtil.getInputStream().available();
                                byte[] msg = new byte[size];
                                applicationUtil.getInputStream().read(msg);
                                MessageStruct messageStruct = new MessageStruct();
                                messageStruct.setMsg(msg);
                                messageStruct.setMessageDate(new Date());
                                messageStruct.setIsReceive(true);
                                historyMessage.add(messageStruct);
                                setHistoryMessageIsRead(false);
                                processMessage.setMessageStruct(messageStruct);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    } catch(IOException e){
                        e.printStackTrace();
                    }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class ProcessMessage{
        MessageStruct messageStruct;

        public void setMessageStruct(MessageStruct messageStruct) {
            this.messageStruct = messageStruct;
            String msgString=new String(messageStruct.msg);


            Gson gson=new Gson();

            MessageJson messageJson = null;
            try {
                messageJson = gson.fromJson(msgString, MessageJson.class);
                Log.e("SOCKETMODEL","the json is ok");
                switch (messageJson.getType())
                {
                    case "command":

                        break;
                    case "message":
                        if(reciveMessageInterface!=null)
                        {
                            reciveMessageInterface.onReciveJsonMessage(messageStruct.msg);

                        }


                        break;
                }

            } catch (JsonSyntaxException e) {
                Log.e("SOCKETMODEL","the json is null");
                if(reciveMessageInterface!=null)
                {
                    reciveMessageInterface.onReciveTextMessage(messageStruct.msg);

                }
                e.printStackTrace();
            }
//            if(messageJson==null)
//            {
//                Log.e("SOCKETMODEL","the json is null");
//            }
//            else
//            {
//                Log.e("SOCKETMODEL","the json is ok");
//            }



        }


    }
}
