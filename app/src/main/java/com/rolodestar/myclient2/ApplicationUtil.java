package com.rolodestar.myclient2;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;


public class ApplicationUtil extends Application {
    private Socket socket=null;
    private OutputStream outputStream=null;
    private InputStream inputStream=null;
    private String address;
    private int port;
    private static String TAG="APPLICATION";


    public ApplicationUtil() {
        super();
        Log.e(TAG,"in construct func");
        address="192.168.5.33";
        port=7777;


//        serverInfo = getSharedPreferences(serverInfoFileName, MODE_PRIVATE);
//        getData();
    }




    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public OutputStream getOutputStream() {
        if(socket==null)
            return null;
        try {
            outputStream=socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public InputStream getInputStream() {
        if(socket==null)
            return null;
        try {
            inputStream=socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        //connectToHost();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
        //connectToHost();
    }

    class ConnectThread extends Thread
    {
        @Override
        public void run() {
            Log.e(TAG,"in connect thread func");
            super.run();
            try {
                if(socket!=null)
                {

                     disConnectToHost();


                }
                socket=new Socket(address,port);
//                InetSocketAddress inetSocketAddress=new InetSocketAddress(address,port);
//                socket.connect(inetSocketAddress,1000);
                outputStream=socket.getOutputStream();
                inputStream=socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class DisConnectThread extends Thread
    {
        @Override
        public void run() {
            Log.e(TAG,"in dis connect func");
            super.run();
            try {
                if(socket!=null)
                {

                    socket.shutdownInput();
                    socket.shutdownOutput();
                    socket.close();
                    socket=null;


                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class SendMessageThread extends Thread
    {
        private byte[]msg=null;


        public byte[] getMsg() {
            return msg;
        }

        public void setMsg(byte[] msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            Log.e(TAG,"in send thread func");
            super.run();
            if(msg!=null)
            {
                if(socket!=null)
                {
                    try {
                        outputStream.write(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    public void onCreate() {

//        address="192.168.5.33";
//        port=7777;
//        serverInfoFileName="serverInfo";
//        serverInfo = getSharedPreferences(serverInfoFileName, MODE_PRIVATE);
//        getData();
        Log.e(TAG,"in create func");
        super.onCreate();
        connectToHost();

    }

    @Override
    public void onTerminate() {
        Log.e(TAG,"in terminate func");

        if(socket!=null)
        {
            if(!socket.isClosed())
            {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
       // setData();
        super.onTerminate();
    }

    public void connectToHost()
    {
        new ConnectThread().start();

    }
    public void disConnectToHost()
    {
        new DisConnectThread().start();
    }
    public void sendMessageToHost(byte[] msg)
    {
        SendMessageThread sendMessageThread=new SendMessageThread();
        sendMessageThread.setMsg(msg);
        sendMessageThread.start();
    }
}
