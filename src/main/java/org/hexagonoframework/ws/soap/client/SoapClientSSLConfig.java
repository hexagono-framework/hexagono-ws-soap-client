package org.hexagonoframework.ws.soap.client;

import javax.net.ssl.*;
import javax.xml.ws.BindingProvider;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class SoapClientSSLConfig {

    private static final String SSL_SOCKET_FACTORY = "com.sun.xml.internal.ws.transport.https.client.SSLSocketFactory";

    private KeystoreConfig keystoreConfig;
    private KeystoreConfig truststoreConfig;

    public SoapClientSSLConfig(KeystoreConfig keystoreConfig, KeystoreConfig truststoreConfig) {
        this.keystoreConfig = keystoreConfig;
        this.truststoreConfig = truststoreConfig;
    }

    public void configure(BindingProvider bindingProvider) {
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(getKeyManagers(), getTrustManagers(), null);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }

        bindingProvider.getRequestContext().put(SSL_SOCKET_FACTORY, sslContext.getSocketFactory());
    }

    private KeyManager[] getKeyManagers() {
        FileInputStream keystoreStream = null;
        KeyManagerFactory kmf;
        try {
            keystoreStream = new FileInputStream(keystoreConfig.getPath());
            KeyStore keystore = KeyStore.getInstance(keystoreConfig.getType().name());
            keystore.load(keystoreStream, keystoreConfig.getPassword().toCharArray());
            kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keystore, keystoreConfig.getPassword().toCharArray());
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException |
                UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (null != keystoreStream) {
                    keystoreStream.close();
                }
            } catch (IOException e) {
                // ignore
            }
        }

        return kmf.getKeyManagers();
    }

    private TrustManager[] getTrustManagers() {
        FileInputStream truststoreStream = null;
        TrustManagerFactory tmf;
        try {
            truststoreStream = new FileInputStream(truststoreConfig.getPath());
            KeyStore truststore = KeyStore.getInstance(truststoreConfig.getType().name());
            truststore.load(truststoreStream, truststoreConfig.getPassword().toCharArray());
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(truststore);
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (null != truststoreStream) {
                    truststoreStream.close();
                }
            } catch (IOException e) {
                // ignore
            }
        }

        return tmf.getTrustManagers();
    }
}
