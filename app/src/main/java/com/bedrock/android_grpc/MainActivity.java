package com.bedrock.android_grpc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bedrock.android_grpc.grpc.gRpcChannelUtils;

import io.grpc.ManagedChannel;

import io.grpc.stub.StreamObserver;


public class MainActivity extends AppCompatActivity {

    EditText etHost,etPort,etMsg;
    Button btnSend;
    TextView tvResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etHost = findViewById(R.id.et_host);
        etPort = findViewById(R.id.et_port);
        etMsg = findViewById(R.id.et_message);
        tvResponse = findViewById(R.id.tv_response);

        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest();

            }
        });

    }

    GreeterGrpc.GreeterStub mStub;

    private void sendRequest(){
        String host = etHost.getText().toString();
        String port = etPort.getText().toString();
        String msg = etMsg.getText().toString();
        if(host.isEmpty() || port.isEmpty() || msg.isEmpty()){
            Toast.makeText(this, "can not empty", Toast.LENGTH_SHORT).show();
            return;
        }
        tvResponse.setText("");
        //普通通道
        final ManagedChannel channel = gRpcChannelUtils.newChannel(host,Integer.parseInt(port));
        ///构建服务api代理   GreeterGrpc自动生成
        mStub = GreeterGrpc.newStub(channel);
        //构建请求体
        HelloRequest request = HelloRequest.newBuilder().setName(msg).build();
        //进行请求
        mStub.sayHello(request, new StreamObserver<HelloReply>() {
            @Override
            public void onNext(final HelloReply value) {
                Log.i("on next", " value : " + value.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvResponse.setText(value.getMessage());
                    }
                });

            }

            @Override
            public void onError(final Throwable t) {
                Log.i("on error", "____" + t.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvResponse.setText(t.getMessage());
                    }
                });

            }

            @Override
            public void onCompleted() {
                Log.i("on Complete", "__________");
                gRpcChannelUtils.shutdown(channel);

            }
        });

    }


}
