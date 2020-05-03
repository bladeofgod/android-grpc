package com.bedrock.android_grpc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.grpc.ManagedChannel;
import io.grpc.examples.helloworld.GreeterGrpc;

public class MainActivity extends AppCompatActivity {

    EditText etHost,etPort,etMsg;
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etHost = findViewById(R.id.et_host);
        etPort = findViewById(R.id.et_port);
        etMsg = findViewById(R.id.et_message);

        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest();

            }
        });

    }

    private void sendRequest(){
        String host = etHost.getText().toString();
        String port = etPort.getText().toString();
        String msg = etMsg.getText().toString();
        if(host.isEmpty() || port.isEmpty() || msg.isEmpty()){
            Toast.makeText(this, "can not empty", Toast.LENGTH_SHORT).show();
            return;
        }

        final ManagedChannel channel =

    }


}
