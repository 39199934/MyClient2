package com.rolodestar.myclient2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.rolodestar.myclient2.R;

import java.net.Socket;

public class SettingHostActivity extends AppCompatActivity {
    EditText cAddress,cPort;
    public int  RESULTOK=1,RESULTCANCLE=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_host);
        cAddress=(EditText) findViewById(R.id.m_settingAddress);
        cPort=(EditText) findViewById(R.id.m_settingPort);

        Intent callIntent = getIntent();
        String address = callIntent.getStringExtra("address");
        if(address==null)
        {
            address="127.0.0.1";
        }
        int port = callIntent.getIntExtra("port",5666);

        cAddress.setText(address);
        cPort.setText(""+port);
    }

    public void onClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.m_btnSettingOk:
                String addressText = cAddress.getText().toString();
                int portInt = Integer.parseInt(cPort.getText().toString());
                Intent intent=new Intent();
                intent.putExtra("address",addressText);
                intent.putExtra("port",portInt);


                setResult(RESULT_OK,intent);
                finish();

                break;
            case R.id.m_btnSettingCancle:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }

    }
}
