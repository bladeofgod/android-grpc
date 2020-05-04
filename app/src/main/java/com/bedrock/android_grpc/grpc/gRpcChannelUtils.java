package com.bedrock.android_grpc.grpc;

import android.util.Log;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.okhttp.OkHttpChannelBuilder;

/**
 * @author LiJiaqi
 * @date 2020/5/3
 * Description:
 */
public final class gRpcChannelUtils {

    private gRpcChannelUtils(){
        throw new UnsupportedOperationException("you can't instaniate me...");
    }

    /**
     * 构建一条SSLChannel
     *
     * @param host         主机服务地址
     * @param port         端口
     * @param authority    域名
     * @param certificates 证书
     * @return
     */

    public static ManagedChannel newSSLChannel(String host, int port,
                                               String authority, InputStream... certificates){
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(certificates);
        return OkHttpChannelBuilder.forAddress(host, port)
        //overrideAuthority非常重要，必须设置调用
            .overrideAuthority(authority)
                .sslSocketFactory(sslParams.sslSocketFactory)
                .build();
    }

    /**
     * 构建一条普通的Channel
     *
     * @param host 主机服务地址
     * @param port 端口
     * @return
     */
    public static ManagedChannel newChannel(String host,int port){
        Log.i("new channel", " host " + host + "  port  " + port);
        return ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
    }

    /**
     * 关闭Channel
     *
     * @param channel 端口
     * @return
     */
    public static boolean shutdown(ManagedChannel channel){
        if(channel != null){
            try {
                return channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            }catch (Exception e){
                e.printStackTrace();;
            }
        }
        return false;
    }


}
















