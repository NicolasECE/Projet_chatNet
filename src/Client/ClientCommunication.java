/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import SHARE.ClientRequest;
import SHARE.ClientStatut;
import SHARE.ServerAnswer;
import SHARE.TopicMessage;
import Server.Topic;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Gestion du socket client et des flux IO Avec le serveur
 * @author labon
 */

public class ClientCommunication
{
    protected ServerAnswer S1=null;
    protected Socket S=null;
    protected ObjectInputStream fromServer; 
    protected ObjectOutputStream toServer;
    protected Scanner sc=new Scanner(System.in);
    protected String topicName;
    protected String clientID;
    protected ClientStatut statut=ClientStatut.OFFLINE;
    
    protected int actualTopicSize;
    
    protected int port= 3000;
    Scanner clientInput = new Scanner(System.in);

    public ClientCommunication() 
    {
        try {
            System.out.println("Trying to connect ...");
            this.S=new Socket(InetAddress.getLocalHost(), this.port);
            System.out.println("Connection to server successful ;");
            
            this.fromServer= new ObjectInputStream(this.S.getInputStream());           
            System.out.println("-InputStream initiated; ");           
            this.toServer= new ObjectOutputStream(this.S.getOutputStream());
            System.out.println("-OutputStream initiated; ");            
            
            this.statut=ClientStatut.NOTCONNECTED;
            
        } catch (IOException ex) {
            System.out.println("Stream creation failure: \n");
            ex.printStackTrace();
        }
        
        
    }
         
    public ClientStatut getStatut() 
    {
        return statut;
    }    
    
    public void setStatut(ClientStatut statut) 
    {
        this.statut = statut;
    }

    public boolean CreateAccount()
    {
        ServerAnswer S1=null;
        try
        {
            toServer.writeObject(ClientRequest.CREATE_ACCOUNT);
            ServerAnswer S= (ServerAnswer) fromServer.readObject();
            if(S.equals(ServerAnswer.READY)) //Server reponding
            {                      
                System.out.println("--- Account creation MENU ---");
                System.out.println("New ID: ");
                String ID= sc.nextLine();
                System.out.println("New Password: ");
                String PW= sc.nextLine();
                String[] account= {ID , PW};
                toServer.writeObject(account);
                
                S1=(ServerAnswer) fromServer.readObject();          //Server reponding
            }
            else 
            {
                System.out.println("Server not ready ;");
                System.out.println("Press RETURN to get back to MENU");
                sc.nextLine();
            }
        } 
        catch(Exception ex)
        {
            System.out.println("Communication to server failed ; ");
            ex.printStackTrace();
           }
        if(S1 == ServerAnswer.ACCOUNT_CREATION_SUCCESSFUL) 
        {
            System.out.println("--Account correctly created ;");
            System.out.println("Press RETURN to get back to MENU");
            sc.nextLine();
            return true;
        }
        else 
        {
            System.out.println("--Account creation failed ;");
            System.out.println("Press RETURN to get back to MENU");
            sc.nextLine();
            return false;
        }
    }   
    
    public void Connect() 
    {
        System.out.println("------ Connection  MENU ------");
        System.out.println("User ID: ");
        String ID= sc.nextLine();
        System.out.println("Password: ");
        String PW= sc.nextLine();
        String[] account= {ID , PW};
        try
        {
            toServer.writeObject(ClientRequest.CONNECT);
            if(fromServer.readObject().equals(ServerAnswer.READY))
            {
                toServer.writeObject(account);
                if(fromServer.readObject().equals(ServerAnswer.CONNECTION_SUCCESSFUL))//Server's answer after check ID PW
                { 
                    this.statut=ClientStatut.CONNECTED;
                    this.clientID = ID;
                    System.out.println("-Connection successful");
                    System.out.println("Press RETURN to get back to MENU");
                    sc.nextLine();
                }
                else 
                {
                    this.statut=ClientStatut.NOTCONNECTED;
                    System.out.println("-Wrong ID and/or Password, connection failed ;");
                    System.out.println("Press RETURN to get back to MENU");
                    sc.nextLine();
                }
            }
            else 
            {
                System.out.println("-Server not ready for communication ; ");
                System.out.println("Press RETURN to get back to MENU");
                sc.nextLine();
            }
            
        }
        catch(Exception ex)
        {
            System.out.println("-Communication failed :\n");
            ex.printStackTrace();
        }
    }    
    
    public void CreateTopic(String topicName)
    {
        try
        {
            toServer.writeObject(ClientRequest.TOPIC_CREATE);
           
            if(fromServer.readObject().equals(ServerAnswer.READY))
            {
                toServer.writeObject(topicName); 
                if(fromServer.readObject().equals(ServerAnswer.TOPIC_CREATION_SUCCESSFUL)) 
                {
                    System.out.println("-Topic creation successful\t;\n"
                        + "--Topic " + topicName + " created\t;");
                    this.topicName = topicName;
                    System.out.println("Press RETURN to get back to MENU");
                    sc.nextLine();
                    
                    ListTopics();
                }      
                else 
                {
                    System.out.println("-Topic creation failed\t\t\t;");
                    System.out.println("Press RETURN to get back to MENU");
                    sc.nextLine();
                }
            } 
            else 
            {
                System.out.println("Server not ready ;");
                System.out.println("Press RETURN to get back to MENU");
                sc.nextLine();
            }
            
        } 
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }    
    
    public void ListTopics()
    {
        int size = 0;
        try
        {
            toServer.writeObject(ClientRequest.TOPIC_LIST);
            if(fromServer.readObject().equals(ServerAnswer.READY))
            {
                toServer.writeObject(ClientRequest.GO);
                size =(int) fromServer.readObject();
                String[] list = new String[size];
                toServer.writeObject(ClientRequest.GO);
                list = (String[]) fromServer.readObject();
                System.out.println(size + " existing topic(s):");
                for(int i=0; i<list.length; i++)
                {
                    System.out.println("> Topic " + (i+1) + ": " + list[i]);
                }
                System.out.println("Press RETURN to get back to MENU");
                sc.nextLine();
            } 
            else 
            {
                System.out.println("No topic in the list;");
                System.out.println("Press RETURN to get back to MENU");
                sc.nextLine();
            }
        } 
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    } 
    
    public void JoinTopic(String topicName)
    {
        try
        {
            toServer.writeObject(ClientRequest.TOPIC_JOIN);
            if(fromServer.readObject().equals(ServerAnswer.READY))
            {
                toServer.writeObject(topicName); 
                if(fromServer.readObject().equals(ServerAnswer.SUCCESS))
                {
                    this.topicName=topicName;
                    this.statut=ClientStatut.IN_TOPIC;
                } 
                else 
                {
                    System.out.println("-Unable to reach this topic you've chosen ;");
                    System.out.println("Press RETURN to get back to MENU");
                    sc.nextLine();
                }
            } 
            else 
            {
                System.out.println("Server not ready ;");
                System.out.println("Press RETURN to get back to MENU");
                sc.nextLine();
            }
        } 
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        
    }

    public void SendMessage(String message)
    {
        TopicMessage MSG = new TopicMessage(this.clientID, message);
        try 
        {
            toServer.writeObject(ClientRequest.NEW_MESSAGE); 
            S1 = (ServerAnswer) fromServer.readObject();
            System.out.println("SERVER ANSWER: " + S1.toString());
            if(S1.equals(ServerAnswer.READY))
            {
                toServer.writeObject(MSG);
//                this.actualMessages.add(MSG);
            }
            else if(S1.equals(ServerAnswer.NOTREADY)) 
            {
                System.out.println("Press RETURN to get back to MENU");
                sc.nextLine();
            }
        } 
        catch(Exception IO)
        {
            System.out.println("Exception thrown in message sending.");
            IO.printStackTrace();
        }   
    }
    
    public void PrintTopic(String[] messages, String[] clients)
    {
        System.out.println("TOPIC# " + this.topicName + "------------------------------------------------------- EXIT to leave chatroom");
        for(int i = 0; i < messages.length; i++)
        {
            System.out.println("> " + clients[i] + " posted: ");
            System.out.println("\t" + messages[i]);
        }
    }
    
    public void getTopic() 
    {
        try 
        {
           toServer.writeObject(ClientRequest.GET_TOPIC); 
           if(fromServer.readObject().equals(ServerAnswer.READY))
           {
               toServer.writeObject(ClientRequest.GO);
               int topicSize = (int) fromServer.readObject();
               String[] messages;
               String[] clients;
               toServer.writeObject(ClientRequest.GO);
               messages = (String[]) fromServer.readObject();
               toServer.writeObject(ClientRequest.GO);
               clients = (String[]) fromServer.readObject();
               PrintTopic(messages, clients);
           }
           else
           {
               System.out.println("--Server not ready ;");
           }
        } 
        catch(Exception ex) 
        {
            System.out.println("Impossible to get topic");
            ex.printStackTrace();
        } 
        
        
    }

    public void ExitTopic() 
    {
        try
        {
            toServer.writeObject(ClientRequest.TOPIC_EXIT);
            if(fromServer.readObject().equals(ServerAnswer.SUCCESS))
            {
                System.out.println("Out of topic " + this.topicName);
                this.statut = ClientStatut.CONNECTED;
                this.topicName = null;
                System.out.println("Press RETURN to get back to MENU");
                sc.nextLine();
            }
            else
            {
                System.out.println("Unable to exit topic " + this.topicName);
            }
            
        } 
        catch(Exception IO)
        {
            IO.printStackTrace();
        }
    }
    
    public void Exit() 
    {
        try
        {
            this.toServer.writeObject(ClientRequest.EXIT);
            this.fromServer.close();
            this.toServer.flush();
            this.toServer.close();
            this.S.close(); 
            this.statut=ClientStatut.OFFLINE;
            System.out.println("End of connection.");
        } 
        catch(IOException IO)
        {
            System.out.println("Diconnection failed: \n");
            IO.printStackTrace();
        }
        
        
    } 
    
    public void ShutDown()
    {
        try{
            this.toServer.writeObject(ClientRequest.SHUTDOWN); 
            this.fromServer.close();
            this.toServer.flush();
            this.toServer.close();
            this.S.close(); 
            this.statut=ClientStatut.OFFLINE;
            System.out.println("End of connection.");
        } catch(IOException IO){
            System.out.println("Diconnection failed: \n");
            IO.printStackTrace();
        }
    }
    
    
    
    
    
    
}
 