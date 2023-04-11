package com.adolfosc.server;

import java.util.List;

/**
 *
 * @author hectoradolfo
 */
public class NewClient {
    
    
    public Client addClient(List<Client> clients, String mac) {
        
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getMac().equalsIgnoreCase(mac) && clients.get(i).isStatus()) {
                return clients.get(i);
            }
        }       
        
        return null;        
    }
}
