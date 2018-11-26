package com.rolodestar.myclient2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity {
    SocketModel socketModel;
    ListView historyChat;
    EditText msgForSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        historyChat=(ListView)findViewById(R.id.m_chatHistory);
        msgForSend=(EditText) findViewById(R.id.m_msgSend);
        socketModel=new SocketModel((ApplicationUtil) getApplication());

        String charSet = socketModel.getCharSet();
        Toast.makeText(this,"the char set:"+charSet,Toast.LENGTH_LONG).show();
    }

    public void onClicked(View view) {
        String msg = msgForSend.getText().toString();
        socketModel.sendAndBuildJsonMessage(msg);
        msgForSend.setText("");
    }
}
