package com.adolfosc.server;

/**
 *
 * @author hectoradolfo
 */
public class Client {
    
    private String ip;
    private String mac;
    private boolean status;

    public Client(String ip, String mac, boolean status) {
        this.ip = ip;
        this.mac = mac;
        this.status = status;
    }

    public String getIp() {
        return ip;
    }

    public String getMac() {
        return mac;
    }

    public boolean isStatus() {
        return status;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }       
    
}
