/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SHARE;

import java.io.Serializable;

/**
 *
 * @author labon
 */
public class TopicMessage implements Serializable{
    static final long serialVersionUID = 1L;
    
    protected volatile String clientID;
    protected volatile String message;

    public TopicMessage(String clientID, String message) {
        this.clientID = clientID;
        this.message = message;
    }

    public synchronized String getClientID() {
        return clientID;
    }

    public synchronized String getMessage() {
        return message;
    }
    
    
    
}
