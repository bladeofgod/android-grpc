package com.bedrock.android_grpc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bedrock.android_grpc.grpc.gRpcChannelUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;

import io.grpc.ManagedChannelBuilder;
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
                //sendRequest();
                request();


            }
        });

    }
    private void request(){
        String host = etHost.getText().toString();
        String port = etPort.getText().toString();
        String msg = etMsg.getText().toString();
        new GrpcTask(this)
                .execute(host,msg,port);
    }

    private static class GrpcTask extends AsyncTask<String, Void, String> {
        private final WeakReference<Activity> activityReference;
        private ManagedChannel channel;

        private GrpcTask(Activity activity) {
            this.activityReference = new WeakReference<Activity>(activity);
        }

        @Override
        protected String doInBackground(String... params) {
            String host = params[0];
            String message = params[1];
            String portStr = params[2];
            int port = TextUtils.isEmpty(portStr) ? 0 : Integer.valueOf(portStr);
            try {
                channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
                GreeterGrpc.GreeterBlockingStub stub = GreeterGrpc.newBlockingStub(channel);
                HelloRequest request = HelloRequest.newBuilder().setName(message).build();
                HelloReply reply = stub.sayHello(request);
                return reply.getMessage();
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.flush();
                return String.format("Failed... : %n%s", sw);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            Activity activity = activityReference.get();
            if (activity == null) {
                return;
            }
            Log.i("grpc", "________" + result);
        }
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
