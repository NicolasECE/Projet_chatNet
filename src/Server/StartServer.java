/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.File;
import static java.lang.Thread.sleep;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author labon
 */
public class StartServer {

    
    public static void main(String[] args) {
        Socket client = null;
        ServerSocket server;
        AccountManager DBA;
        TopicsManager topicsMg;
        try{
            server=new ServerSocket(3000);
            System.out.println("-Server open on port 3000;");
            
//Creating account DATABASE              
            File f = new File("AccountsList");
            if(f.exists() && !f.isDirectory()){     // déjà un fichier de comptes?
                System.out.println("--Database created ;");
                DBA=new AccountManager(f);
            } else {               
                System.out.println("--Database created ;");
                DBA= new AccountManager("AccountsList");
            }
//-------------------------------------------------------------------------------------------------------------

            topicsMg=new TopicsManager();
            System.out.println("--Topics Manager created ;");
            while(!server.isClosed()){
                System.out.println("\nWaiting for connection...");
                try{
                client=server.accept();
                }
                catch(Exception ex){
                    System.out.println("- Server off -");
                    System.exit(0);
                }
                System.out.println("-New client detected on port " + client.getPort() + " ; ");
                Thread T1= new Thread(new HandleClient(server, client, DBA, topicsMg));
                T1.start();
            }
        } catch(Exception E){
            
            E.printStackTrace();
            
        }
    }
    
}
