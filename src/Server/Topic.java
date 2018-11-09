/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import SHARE.TopicMessage;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author labon
 */
public class Topic implements Serializable {
    
    protected String topicName;
    protected ArrayList<TopicMessage> data = new ArrayList<TopicMessage>();
    protected ArrayList<String> ClientsConnected = new ArrayList<String>();
    
    public Topic(String topicName)
    {
        this.topicName=topicName;
    }
    
    public String getTopicName() 
    {
        return topicName;
    }
    
}
