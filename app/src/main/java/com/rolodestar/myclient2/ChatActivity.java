package com.rolodestar.myclient2;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    SocketModel socketModel;
    ListView historyChat;
    EditText msgForSend;
    Map<String,Object> map;
    List<Map<String,Object>> msgList;
    String[] fromListViw;
    int[] toListview;
    MyAdpter myAdpter;
    String dstName;
    MyHandle myHandle;
    MyReciveMessageInterface myReciveMessageInterface;

    public String getDstName() {
        return dstName;
    }

    public void setDstName(String dstName) {
        this.dstName = dstName;

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
                    addListViewItem("server",new String(recvMsg));
                    //helloWorld.setText(new String(recvMsg));
                    break;
                case 1002:
                    Bundle data = msg.getData();
                    String from = data.getString("from");
                    String to = data.getString("to");
                    String msgBundle = data.getString("msg");
                    addListViewItem(from,msgBundle);


                    break;
            }
            myAdpter.notifyDataSetChanged();
            super.handleMessage(msg);
        }
    }
    class MyReciveMessageInterface implements ReciveMessageInterface
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
           // String viewStr;
            Bundle data=new Bundle();
            Gson gson=new Gson();
            try {
                MessageJson messageJson=gson.fromJson(new String(msg),MessageJson.class);
                data.putString("from",messageJson.from);
                data.putString("to",messageJson.to);
                data.putString("msg",messageJson.msg);
               // viewStr="["+messageJson.from+"]对["+messageJson.to+"]说:"+messageJson.msg;
                //helloWorld.setText(viewStr);
            } catch (JsonSyntaxException e) {
                data.putString("from","server");
                data.putString("to","unknow");
                data.putString("msg",new String(msg));
            }
            //handleMessage.obj=viewStr;

            handleMessage.setData(data);
            myHandle.sendMessage(handleMessage);

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
//        historyChat=(ListView)findViewById(R.id.m_chatHistory);
//        msgForSend=(EditText) findViewById(R.id.m_msgSend);
//
//        myHandle=new MyHandle();
//        myReciveMessageInterface=new MyReciveMessageInterface();
//        socketModel=new SocketModel((ApplicationUtil) getApplication());
//        socketModel.setReciveMessageInterface(myReciveMessageInterface);
//        msgList=new ArrayList<Map<String, Object>>();
//        fromListViw=new String[]{"photo","name","msg"};
//        toListview=new int[]{R.id.m_photo,R.id.m_name,R.id.m_msg};
//        dstName="server";
//        myAdpter=new MyAdpter();
//        historyChat.setAdapter(myAdpter);
//
//        String charSet = socketModel.getCharSet();
//        addListViewItem("ro","hi");
//        Toast.makeText(this,"the char set:"+charSet,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        historyChat=(ListView)findViewById(R.id.m_chatHistory);
        msgForSend=(EditText) findViewById(R.id.m_msgSend);

        myHandle=new MyHandle();
        myReciveMessageInterface=new MyReciveMessageInterface();
        socketModel=new SocketModel((ApplicationUtil) getApplication());
        socketModel.setClientName("rolodestar");
        socketModel.setReciveMessageInterface(myReciveMessageInterface);
        msgList=new ArrayList<Map<String, Object>>();
        fromListViw=new String[]{"photo","name","msg"};
        toListview=new int[]{R.id.m_photo,R.id.m_name,R.id.m_msg};
        dstName="server";
        myAdpter=new MyAdpter();
        historyChat.setAdapter(myAdpter);

        String charSet = socketModel.getCharSet();
        addListViewItem("ro","hi");
        Toast.makeText(this,"the char set:"+charSet,Toast.LENGTH_LONG).show();
    }

    public void addListViewItem(String name, String msg)
    {
        map=new HashMap<String,Object>();
        map.put("photo",R.drawable.head);
        map.put("name",name);
        map.put("msg",msg);
        msgList.add(map);

    }

    public void onClicked(View view) {
        String msg = msgForSend.getText().toString();
        socketModel.sendAndBuildJsonMessage(msg);
        msgForSend.setText("");
        addListViewItem(socketModel.getClientName(),msg);
    }
    class MyAdpter extends SimpleAdapter{

        /**
         * Constructor
         *
         * @param context  The context where the View associated with this SimpleAdapter is running
         * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
         *                 Maps contain the data for each row, and should include all the entries specified in
         *                 "from"
         * @param resource Resource identifier of a view layout that defines the views for this list
         *                 item. The layout file should include at least those named views defined in "to"
         * @param from     A list of column names that will be added to the Map associated with each
         *                 item.
         * @param to       The views that should display column in the "from" parameter. These should all be
         *                 TextViews. The first N views in this list are given the values of the first N columns
         */
        public MyAdpter() {
            super(ChatActivity.this, msgList, R.layout.layout_chat, fromListViw,toListview);
        }
    }
}
