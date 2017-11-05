package org.hexagonoframework.ws.soap.client;

public class KeystoreConfig {

    private String path;
    private String password;
    private KeystoreType type;

    public KeystoreConfig(String path, String password, KeystoreType type) {
        this.path = path;
        this.password = password;
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public String getPassword() {
        return password;
    }

    public KeystoreType getType() {
        return type;
    }
}
