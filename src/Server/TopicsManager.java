/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import SHARE.TopicMessage;
import java.util.ArrayList;

/**
 *
 * @author labon
 */
public class TopicsManager {
    

    protected volatile ArrayList<Topic> topicsList = new ArrayList<Topic>();    
    protected volatile ArrayList<String> messagesToPrint = new ArrayList<String>();    
    protected boolean isOn;
    protected boolean isNewMessage;
    
    public TopicsManager() 
    {
        this.isOn = true;
        this.isNewMessage= false;
    }  
    
        
    public synchronized boolean JoinTopic(String topic, String client)
    {
        if(topicsList.isEmpty())
        {
            return false;
        }
        else 
        {
            for (int i = 0; i < this.topicsList.size(); i++)
            {
                if(this.topicsList.get(i).topicName.equals(topic))
                {
                    this.topicsList.get(i).ClientsConnected.add(client);
                    return true;
                }
            }
        }
        return false;
    }
    
    public synchronized boolean CreateTopic(String topicName, String ClientID)
    {
        for(int i=0; i<topicsList.size(); i++)
        {
            if(topicsList.get(i).topicName.equals(topicName))
            {
                return false;
            }
        }
        Topic top= new Topic(topicName);
        topicsList.add(top);
        return true;
    }
    
    public synchronized boolean ExitTopic(String ClientID,String topicName)
    {
        if(!topicsList.isEmpty())
        {
            for(int i = 0; i < topicsList.size(); i++)
            {
                if(topicsList.get(i).topicName.equals(topicName))
                {
                    if(DisconnectClient(i, ClientID))
                    {
                        return true;
                    }
                }
            }    
        }
        return false;
    }
    
    public ArrayList<Topic> getTopicsList() 
    {
        return topicsList;
    }

    public synchronized boolean DisconnectClient(int rank, String ClientID){
        if(topicsList.get(rank).ClientsConnected.isEmpty())
        {
            return false;
        } 
        else 
        {
            for(int i=0 ; i< topicsList.get(rank).ClientsConnected.size(); i++)
            {
                if(topicsList.get(rank).ClientsConnected.get(i).equals(ClientID))
                {
                    topicsList.get(rank).ClientsConnected.remove(i);
                    return true;
                }
            }
        }
        return false;
    }
    
    public synchronized void setIsOn(boolean isOn) 
    {
        this.isOn = isOn;
    }
    
    public synchronized void AddMsg(String topicName, TopicMessage message)
    {
        for(int i = 0 ; i < this.topicsList.size() ; i++)
        {
            this.topicsList.get(i).data.add(message);
        }
    }
    
    public synchronized void ConnectClient(String clientID, String topicName) 
    {
        for(int i= 0; i < topicsList.size(); i++)
        {
            if(this.topicsList.get(i).topicName.equals(topicName)) this.topicsList.get(i).ClientsConnected.add(clientID);
        }
    }
}
    
  
