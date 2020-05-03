package com.bedrock.android_grpc.grpc;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

/**
 * @author LiJiaqi
 * @date 2020/5/3
 * Description:
 */
public class HttpsUtils {

    public static  class SSLParams{
        public SSLSocketFactory sslSocketFactory;
        public X509TrustManager trustManager;
    }

    public static SSLParams getSslSocketFactory(){
        return getSslSocketFactoryBase();
    }


    private static SSLParams getSslSocketFactoryBase(X509TrustManager trustManager,
                                                     InputStream bksFile,String password,
                                                     InputStream... certificates){
        SSLParams sslParams = new SSLParams();

        try {
            KeyManager[] keyManagers = pre

        }catch ()

    }

    private static KeyManager[] prepareKeyManager(InputStream bksFile,String password){
        try {
            if(bksFile == null || password == null) return null;
            KeyStore clientKeyStore = KeyStore.getInstance("BKS");
            clientKeyStore.load(bksFile,password.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(
                    KeyManagerFactory.getDefaultAlgorithm());


        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}



















