package com.rolodestar.myclient2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class MainActivity extends AppCompatActivity {

    ApplicationUtil applicationUtil;
    String address;
    int port;
    private String serverInfoFileName;
    SharedPreferences serverInfo;
    SocketModel socketModel;
    MyHandle myHandle;
    TextView helloWorld;
    RecvMessage recvMessage;
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==3001)
        {
            if(resultCode==SettingHostActivity.RESULT_OK)
            {
                String getAddress = data.getStringExtra("address");
                int getPort = data.getIntExtra("port", 5666);
                if(applicationUtil!=null) {
                    applicationUtil.disConnectToHost();
                    applicationUtil.setAddress(getAddress);
                    applicationUtil.setPort(getPort);

                    applicationUtil.connectToHost();
                }

            }
            else if(resultCode==SettingHostActivity.RESULT_CANCELED)
            {

            }
        }
        if(requestCode==3002)
        {
            //service.setRecvInterFace(interFace);
            if(socketModel!=null)
            {
                socketModel.setReciveMessageInterface(recvMessage);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.m_menuConnect:
                if(applicationUtil!=null)
                {
                    applicationUtil.connectToHost();
                }

                break;

            case R.id.m_menuDisConnect:
                if(applicationUtil!=null)
                {
                    applicationUtil.disConnectToHost();
                }
                break;
            case R.id.m_menuViewHostInfo:
                if(applicationUtil!=null) {


                    String address = applicationUtil.getAddress();
                    int port =applicationUtil.getPort();
                    Toast.makeText(this, "host Info,address:" + address + ",the port:" + port, Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.m_menuSetHostInfo:
                if(applicationUtil!=null) {
                    String setAddress = applicationUtil.getAddress();
                    int setPort =applicationUtil.getPort();
                    Intent setIntent = new Intent(this, SettingHostActivity.class);
                    Bundle setBundle = new Bundle();

                    setIntent.putExtra("address", setAddress);
                    setIntent.putExtra("port", setPort);
                    startActivityForResult(setIntent, 3001);
                }


                break;
            case R.id.m_startChatActivity:
                Intent chatIntent=new Intent(MainActivity.this,ChatActivity.class);
                startActivityForResult(chatIntent,3002);
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    class MyHandle extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            byte[] recvMsg;
            switch (msg.arg1)
            {
                case 1001:
                    recvMsg=(byte[]) msg.obj;
                    helloWorld.setText(new String(recvMsg));
                    break;
                case 1002:

                    String viewStr=(String) msg.obj;
                    helloWorld.setText(viewStr);
                    break;
            }
            super.handleMessage(msg);
        }
    }
    class RecvMessage implements ReciveMessageInterface
    {

        @Override
        public void onReciveTextMessage(byte[] msg) {

            Log.e("interface","the id:"+Thread.currentThread().getId());
            Message handleMessage=new Message();
            handleMessage.arg1=1001;
            handleMessage.obj=msg;
            myHandle.sendMessage(handleMessage);



        }

        @Override
        public void onReciveJsonMessage(byte[] msg) {
            Log.e("interface","the id:"+Thread.currentThread().getId());
            Message handleMessage=new Message();
            handleMessage.arg1=1002;
            String viewStr;
            Gson gson=new Gson();
            try {
                MessageJson messageJson=gson.fromJson(new String(msg),MessageJson.class);
                viewStr="["+messageJson.from+"]对["+messageJson.to+"]说:"+messageJson.msg;
                //helloWorld.setText(viewStr);
            } catch (JsonSyntaxException e) {
                viewStr="数据解析错误，内容为:"+new String(msg);
            }
            handleMessage.obj=viewStr;
            myHandle.sendMessage(handleMessage);

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helloWorld=(TextView) findViewById(R.id.m_hello);
        recvMessage=new RecvMessage();
        myHandle=new MyHandle();

        serverInfoFileName="serverInfo";
        //new GetSharedPreferencesThread().start();
        serverInfo=getSharedPreferences(serverInfoFileName,MODE_PRIVATE);
        //serverInfo.edit().putString("test","test").commit();

        address="192.168.5.33";
        port=5666;
        getData();
        applicationUtil=(ApplicationUtil) getApplication();
        applicationUtil.setAddress(address);
        applicationUtil.setPort(port);
        applicationUtil.disConnectToHost();
        applicationUtil.connectToHost();
        MessageJson messageJson=new MessageJson();
        messageJson.setFrom("client");
        messageJson.setTo("server");
        messageJson.setHead("127.0.0.1");
        messageJson.setType("message");
        messageJson.setMsg("hi,server");
        Gson gson=new Gson();
        String msgJson = gson.toJson(messageJson);

        applicationUtil.sendMessageToHost("hi,server from main activity".getBytes());
        socketModel=new SocketModel(applicationUtil);
        socketModel.setReciveMessageInterface(recvMessage);
        socketModel.sendMessage(msgJson);
        socketModel.setRunning(false);


    }

    @Override
    protected void onDestroy() {
        this.address=applicationUtil.getAddress();
        this.port=applicationUtil.getPort();
        applicationUtil.disConnectToHost();
        setData();
        super.onDestroy();
    }

    private void getData()
    {
        if(serverInfo==null)
            return;
        String addressRead = serverInfo.getString("address",this.address);
        int portRead = serverInfo.getInt("port", this.port);
        this.address=addressRead;
        this.port=portRead;
    }
    private void setData()
    {
        if(serverInfo==null)
            return;
        serverInfo.edit().putString("address",this.address);
        serverInfo.edit().putInt("port",this.port);
        serverInfo.edit().commit();
    }

    public String getServerInfoFileName() {
        return serverInfoFileName;
    }

    public void setServerInfoFileName(String serverInfoFileName) {
        this.serverInfoFileName = serverInfoFileName;
    }
    public void onClicked(View view)
    {
        switch ((view.getId()))
        {
            case R.id.m_btnConnect:
                if(applicationUtil!=null)
                {
                    applicationUtil.connectToHost();
                }
                break;
            case R.id.m_btnDisConnect:
                if(applicationUtil!=null)
                {
                    applicationUtil.disConnectToHost();
                }
                break;
            case R.id.m_btnStartChat:
                Intent intent=new Intent(MainActivity.this,ChatActivity.class);
                startActivity(intent);
                break;

        }

    }

}
