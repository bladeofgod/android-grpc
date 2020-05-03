package com.bedrock.android_grpc.grpc;

import java.io.InputStream;

import io.grpc.ManagedChannel;

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
        Htt
    }


}
















