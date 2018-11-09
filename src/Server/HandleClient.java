/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import SHARE.TopicMessage;
import SHARE.ClientRequest;
import SHARE.ServerAnswer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author labon
 */


// Attend la connexion d'un client en permanence

public class HandleClient implements Runnable {
   
    protected ServerSocket server1;
    protected Socket client;
    protected AccountManager DBA;
    protected TopicsManager TM;
    protected String clientID;
    protected String currentTopic; 
    
    protected ObjectOutputStream toClient = null;
    protected ObjectInputStream fromClient = null;

    public HandleClient(ServerSocket server1, Socket client, AccountManager DBA, TopicsManager TM) {
        this.server1=server1;
        this.client=client;
        this.DBA=DBA;
        this.TM=TM;
    }

    @Override
    public void run() 
    { /// Gestion des requetes client

        try
        {
            toClient = new ObjectOutputStream(client.getOutputStream()); 
            fromClient = new ObjectInputStream(client.getInputStream());
            System.out.println("Handle client: Streams created for connection on port "+ client.getPort() +" ;\n");
        } 
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
        while(!client.isClosed())
        {
            try
            {
                ClientRequest request= (ClientRequest) fromClient.readObject();
                System.out.println("CLIENT REQUEST ON PORT " + this.client.getPort() + ": " + request.toString());
                switch(request)
                {
                    case CONNECT:
                        toClient.writeObject(ServerAnswer.READY);
                        String[] accountToConnect= (String[]) fromClient.readObject();
                        String ID=accountToConnect[0];
                        String PW=accountToConnect[1];
                        if(DBA.ConnectionCheck(ID, PW))
                        {
                            toClient.writeObject(ServerAnswer.CONNECTION_SUCCESSFUL);
                            this.clientID = ID;
                        } 
                        else 
                        {
                            toClient.writeObject(ServerAnswer.CONNECTION_FAILED);
                        }
                        break;
                        
                    case CREATE_ACCOUNT:
                        toClient.writeObject(ServerAnswer.READY);
                        String[] accountToCreate= (String[]) fromClient.readObject();
                        String newID=accountToCreate[0];
                        String newPW=accountToCreate[1];
                        System.out.println(newID);
                        System.out.println(newPW);
                        if(DBA.CreateAccount(newID, newPW))
                        {
                            toClient.writeObject(ServerAnswer.ACCOUNT_CREATION_SUCCESSFUL);
                            this.clientID = newID;
                        } 
                        else 
                        {
                            toClient.writeObject(ServerAnswer.ACCOUNT_CREATION_FAILED);
                        }
                        break;
                        
                    case TOPIC_LIST: // Expliquer les soucis de passange dans le flux - méthode barbare car après debus, corruption de l'arraylist dans le transfert
                        
                        
                        String[] topicListString = new String[this.TM.getTopicsList().size()];
                        if(this.TM.getTopicsList().isEmpty())
                        {
                            toClient.writeObject(ServerAnswer.NOTREADY);
                        } 
                        else 
                        {
                            for(int i = 0; i < this.TM.getTopicsList().size(); i++)
                            {
                                topicListString[i] = this.TM.getTopicsList().get(i).getTopicName();
                            }
                            for(int i = 0; i< this.TM.getTopicsList().size(); i++)
                            {
                                System.out.println("Topic " + (i+1) + ": " + this.TM.getTopicsList().get(i).getTopicName());
                            }
                            toClient.writeObject(ServerAnswer.READY);
                            if(fromClient.readObject().equals(ClientRequest.GO))
                            {
                                toClient.writeObject(topicListString.length); 
                                if(fromClient.readObject().equals(ClientRequest.GO))
                                {
                                    toClient.writeObject(topicListString);                                
                                }
                            }
                        }
                        break;
                        
                    case TOPIC_CREATE:                      
                        toClient.writeObject(ServerAnswer.READY);
                        String topicToCreate= (String) fromClient.readObject();
                        if(TM.CreateTopic(topicToCreate, this.clientID)) 
                        {
                            toClient.writeObject(ServerAnswer.TOPIC_CREATION_SUCCESSFUL);                            
                        } 
                        else 
                        {
                            toClient.writeObject(ServerAnswer.TOPIC_CREATION_FAILED);
                        }
                        break;
                        
                    case TOPIC_JOIN:
                        toClient.writeObject(ServerAnswer.READY);
                        String tn= (String) fromClient.readObject();
                        if(TM.JoinTopic(tn, this.clientID))
                        {
                            this.currentTopic = tn;
                            toClient.writeObject(ServerAnswer.SUCCESS);
                        }
                        else 
                        {
                            toClient.writeObject(ServerAnswer.FAIL);
                        }
                        break;
                        
                    case NEW_MESSAGE:
                        toClient.writeObject(ServerAnswer.READY);
                        TopicMessage msg = (TopicMessage) fromClient.readObject();
                        this.TM.AddMsg(this.currentTopic , msg);
                        break;
                        
                    case TOPIC_EXIT:
                        if(this.TM.ExitTopic(this.clientID, this.currentTopic))
                        {
                            toClient.writeObject(ServerAnswer.SUCCESS);
                        } 
                        else
                        {
                            toClient.writeObject(ServerAnswer.FAIL);
                        }
                        break; 
                        
                    case GET_TOPIC :
                        int i = 0;
                        toClient.writeObject(ServerAnswer.READY);
                        
                        if(fromClient.readObject().equals(ClientRequest.GO))
                        {
                            toClient.writeObject(this.TM.getTopicsList().get(i).data.size());
                            
                            while(!this.TM.topicsList.get(i).topicName.equals(this.currentTopic))
                            {
                               i = i + 1;
                            }
                            if(this.TM.topicsList.get(i).topicName.equals(this.currentTopic))
                            {
                                String[] messages = new String[this.TM.getTopicsList().get(i).data.size()];
                                String[] clients = new String[this.TM.getTopicsList().get(i).data.size()];
                                for(int j = 0; j<this.TM.getTopicsList().get(i).data.size(); j++)
                                {
                                    messages[j] = this.TM.getTopicsList().get(i).data.get(j).getMessage();
                                    clients[j] = this.TM.getTopicsList().get(i).data.get(j).getClientID();
                                }
                                if(fromClient.readObject().equals(ClientRequest.GO))
                                {
                                    toClient.writeObject(messages);
                                    if(fromClient.readObject().equals(ClientRequest.GO))
                                    {
                                        toClient.writeObject(clients);
                                    }
                                }
                            }
                        }
                        
                        break;
                    case EXIT:
                        Exit();
                        break;
                    case SHUTDOWN:
                        ShutDown();
                        break; 
                    default:
                        System.out.println("WFT is that command?");
                        break;
                }
            } 
            catch(IOException | ClassNotFoundException ex)
            {
                Exit();
            }
        }
    }
    
    public boolean Exit() 
    {
        try{
            System.out.println("Handle client: Request EXIT from client on port " + client.getPort() + " ;");
            this.fromClient.close();
            this.toClient.flush();
            this.fromClient.close();
            this.client.close(); 
            System.out.println("Handle client: Client connection closed ;");
            System.out.println("\nWaiting for connection ...");
            return true;
        } catch(Exception ex){
            ex.printStackTrace();
            return false;        
        }
    }
   
    public void ShutDown()
    {
        try{
            System.out.println("Handle Client: Service SHUTDOWN requested on port " + this.client.getPort() + " ;");
            this.fromClient.close();
            this.toClient.flush();
            this.fromClient.close();
            this.client.close();
            this.server1.close();
        }
         catch(IOException ex) {
             System.out.println("- Shutdown failed");
             ex.printStackTrace();
         }
    }
    
}
    
            

    
