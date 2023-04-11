package com.adolfosc.trama;

import java.io.Serializable;

/**
 *
 * @author hectoradolfo
 */
public class Trama implements Serializable{
    
    private String mac;
    private String ip;
    private String message;
    private Option type;

    public Trama(String mac, String ip, String message, Option type) {
        this.mac = mac;
        this.ip = ip;
        this.message = message;
        this.type = type;
    }

    public Trama() {
    }

    public Option getType() {
        return type;
    }

    public void setType(Option type) {
        this.type = type;
    }
  
    

    public String getMac() {
        return mac;
    }

    public String getIp() {
        return ip;
    }

    public String getMessage() {
        return message;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setMessage(String message) {
        this.message = message;
    }
        
}
