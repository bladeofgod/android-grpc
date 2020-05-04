package com.bedrock.android_grpc.grpc;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * @author LiJiaqi
 * @date 2020/5/3
 * Description:  ssl,证书配置等等
 */
public class HttpsUtils {

    public static  class SSLParams{
        public SSLSocketFactory sslSocketFactory;
        public X509TrustManager trustManager;
    }

    public static SSLParams getSslSocketFactory(){
        return getSslSocketFactoryBase(null,null,null);
    }


    /**
     * https单向认证
     * 可以额外配置信任服务端的证书策略，否则默认是按CA证书去验证的，若不是CA可信任的证书，则无法通过验证
     */
    public static SSLParams getSslSocketFactory(X509TrustManager trustManager) {
        return getSslSocketFactoryBase(trustManager, null, null);
    }

    /**
     * https单向认证
     * 用含有服务端公钥的证书校验服务端证书
     */
    public static SSLParams getSslSocketFactory(InputStream... certificates) {
        return getSslSocketFactoryBase(null, null, null, certificates);
    }

    /**
     * https双向认证
     * bksFile 和 password -> 客户端使用bks证书校验服务端证书
     * certificates -> 用含有服务端公钥的证书校验服务端证书
     */
    public static SSLParams getSslSocketFactory(InputStream bksFile, String password, InputStream... certificates) {
        return getSslSocketFactoryBase(null, bksFile, password, certificates);
    }

    /**
     * https双向认证
     * bksFile 和 password -> 客户端使用bks证书校验服务端证书
     * X509TrustManager -> 如果需要自己校验，那么可以自己实现相关校验，如果不需要自己校验，那么传null即可
     */
    public static SSLParams getSslSocketFactory(InputStream bksFile, String password, X509TrustManager trustManager) {
        return getSslSocketFactoryBase(trustManager, bksFile, password);
    }


    private static SSLParams getSslSocketFactoryBase(X509TrustManager trustManager,
                                                     InputStream bksFile,String password,
                                                     InputStream... certificates){
        SSLParams sslParams = new SSLParams();

        try {
            KeyManager[] keyManagers = prepareKeyManager(bksFile, password);
            TrustManager[] trustManagers = prepareTrusManager(certificates);
            X509TrustManager manager;
            if(trustManager != null){
                //优先使用用户定义的trust manager
                manager = trustManager;
            }else if(trustManagers != null){
                //使用默认的 x509格式证书
                manager = chooseTrustManager(trustManagers);
            }else{
                //否则使用不安全的trustmanager
                manager = UnSafeTrustManager;
            }
            //创建TLS类型的SSLContext对象， that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            // 用上面得到的trustManagers初始化SSLContext，这样sslContext就会信任keyStore中的证书
            // 第一个参数是授权的密钥管理器，用来授权验证，比如授权自签名的证书验证。
            // 第二个是被授权的证书管理器，用来验证服务器端的证书
            sslContext.init(keyManagers, new TrustManager[]{manager}, null);
            // 通过sslContext获取SSLSocketFactory对象
            sslParams.sslSocketFactory = sslContext.getSocketFactory();
            sslParams.trustManager = manager;
            return sslParams;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 为了解决客户端不信任服务器数字证书的问题，网络上大部分的解决方案都是让客户端不对证书做任何检查，
     * 这是一种有很大安全漏洞的办法
     */
    public static X509TrustManager UnSafeTrustManager = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    };


    private static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }

    private static TrustManager[] prepareTrusManager(InputStream... certificates){
        if(certificates == null || certificates.length <= 0) return null;

        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");//公钥证书格式
            ///创建一个默认类型的keystore，存储我们信任的证书
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index= 0;
            for(InputStream  certStream :certificates){
                String certificateAlies = Integer.toString(index++);
                //证书工厂根据证书文件的流生成证书
                Certificate cert = certificateFactory.generateCertificate(certStream);
                //将cert作为可信证书放到keystore
                keyStore.setCertificateEntry(certificateAlies, cert);
                try {
                    if(certStream != null) certStream.close();
                }catch (Exception e){}
            }
            //创建一个默认类型的TrustManagerFactory
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            //用之前的keystore初始化tmf，tfm就会信任keystore中的证书
            tmf.init(keyStore);
            //通过tmf获取trustmanager数组，trustManager 也会新人keystore中的证书
            return tmf.getTrustManagers();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    private static KeyManager[] prepareKeyManager(InputStream bksFile,String password){
        try {
            if(bksFile == null || password == null) return null;
            KeyStore clientKeyStore = KeyStore.getInstance("BKS");
            clientKeyStore.load(bksFile,password.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(
                    KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(clientKeyStore, password.toCharArray());
            return kmf.getKeyManagers();


        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return null;
    }


}



















