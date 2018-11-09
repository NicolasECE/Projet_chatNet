/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;


import SHARE.CLS;
import SHARE.ClientStatut;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author labon
 */
public class NewClient {

    public static void main(String Args[]) throws IOException, InterruptedException{

        CLS clear = new CLS();
        Scanner clientInput = new Scanner(System.in);
        BufferedReader BR= new BufferedReader(new InputStreamReader(System.in));
        ClientCommunication lead = null;
        try {
            lead = new ClientCommunication();
            System.out.println("Init ok ;\n");

        } catch (Exception ex) {
            System.out.println("Init failed: \n");
            ex.printStackTrace();
        }

        while (!(lead.getStatut().equals(ClientStatut.OFFLINE))) { //loops as long as the connection lasts (statut != offline)
            clear.clearScreen();
            switch (lead.getStatut()) {
                case NOTCONNECTED:
                    do {
                        clear.clearScreen();                        
                        
                        System.out.println("Your statut: " + lead.getStatut().toString());
                        System.out.println("--------------------MENU---------------------");
                        System.out.println("Create an account   : CREATE_ACCOUNT        ;");
                        System.out.println("Log-in              : CONNECT               ;");
                        System.out.println("Help                : help                  ;");
                        System.out.println("Close client        : EXIT                  ;");
                        System.out.println("\nYour choice: ");
                        String request = clientInput.nextLine().toUpperCase(); // wait for client input
                        switch (request) {
                            case "CREATE_ACCOUNT":
                                lead.CreateAccount();
                                break;
                            case "CONNECT":
                                lead.Connect();
                                break;
                            case "HELP":
                                System.out.println("\n\nYour statut: " + lead.getStatut().toString());
                                System.out.println("--------------------MENU--------------------");
                                System.out.println("Create an account   : CREATE_ACCOUNT    ;");
                                System.out.println("Log-in              : CONNECT           ;");
                                System.out.println("End connection      : EXIT              ;");
                                break;
                            case "EXIT":
                                lead.Exit();
                                break;
                            case "SHUTDOWN":
                                lead.ShutDown();
                                break;
                            default:
                                System.out.println("-Unidentified command ;");
                                System.out.println("-Press RETURN to get back to MENU;");
                                clientInput.nextLine();
                                break;
                        }
                    } while (lead.getStatut().equals(ClientStatut.NOTCONNECTED));
                    break;
                case CONNECTED:
                    do 
                    {                                
                        clear.clearScreen();
                        System.out.println("\n\nYour statut: " + lead.getStatut().toString());
                        System.out.println("--------------------MENU---------------------");
                        System.out.println("Create a TOPIC      : TOPIC_CREATE      ;");
                        System.out.println("Join a TOPIC        : TOPIC_JOIN        ;");
                        System.out.println("List of the TOPICS  : TOPIC_LIST        ;");
                        System.out.println("Logout              : DISCONNECT        ;");
                        System.out.println("Close client        : EXIT              ;");
                        System.out.println("\nYour choice: ");
                        String request = clientInput.nextLine().toUpperCase(); // wait for client input
                        
                        switch (request) {
                            case "TOPIC_CREATE":
                                System.out.println("-----------------TOPIC MENU-----------------");
                                System.out.println("New topic name : ");
                                String newTopic = clientInput.nextLine();
                                lead.CreateTopic(newTopic);
                                break;
                            case "TOPIC_JOIN" :
                                System.out.println("-----------------TOPIC MENU-----------------");
                                System.out.println("Topic name : ");
                                String topicName = clientInput.nextLine();
                                lead.JoinTopic(topicName);
                                break;
                            case "TOPIC_LIST":
                                lead.ListTopics();
                                break;
                            case "DISCONNECT":
                                lead.setStatut(ClientStatut.NOTCONNECTED);
                                lead.clientID = "";
                                System.out.println("You're disconnected.");
                                break;
                            case "help":
                                System.out.println("\n\nYour statut: " + lead.getStatut().toString());
                                System.out.println("--------------------MENU---------------------");
                                System.out.println("Create a TOPIC      : TOPIC_CREATE      ;");
                                System.out.println("Join a TOPIC        : TOPIC_JOIN        ;");
                                System.out.println("List of the TOPICS  : TOPIC_LIST        ;");
                                System.out.println("Logout              : DISCONNECT        ;");
                                System.out.println("End connection      : EXIT              ;");
                                break;
                            case "EXIT":
                                lead.Exit();
                                break;
                            default:
                                System.out.println("-Unidentified command ;");
                                System.out.println("-Press RETURN to get back to MENU;");
                                clientInput.nextLine();
                                break;
                        }
                    } while (lead.getStatut().equals(ClientStatut.CONNECTED));
                    break;
                case IN_TOPIC:
                    String newMessage;
                    lead.getTopic(); 
                    try
                    {
                        newMessage= BR.readLine();
                        if(!newMessage.equals(""))
                        {
                            if(newMessage.toUpperCase().equals("EXIT"))
                            {
                                lead.ExitTopic();
                            } 
                            else 
                            {
                                lead.SendMessage(newMessage);
                            }
                        }
                    } 
                    catch(IOException ex)
                    {
                        System.out.println("TOPIC ERROR");
                        lead.ExitTopic();
                    }
                    break;
            }
        }
        System.out.println("\n\n---END OF CLIENT .");
    }
    

}
