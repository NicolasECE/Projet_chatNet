/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author labon
 */
public class AccountManager {
	private File file;
	protected ArrayList<Account> data = new ArrayList<Account>(); //Serializable arraylist of Accounts.

        
	public AccountManager(String fileName) {          //Creation of the database file
		this.file = new File(fileName);
	}
        
        public AccountManager(File f){
            this.file=f;
            LoadAccounts();
        }
	
        public synchronized boolean ConnectionCheck(String ID, String PW){
            for(int i=0; i<data.size(); i++){
                if(data.get(i).getID().equals(ID)&&data.get(i).getPW().equals(PW)){
                    return true;
                }
            }
            return false;
        }
        
	public synchronized boolean CreateAccount(String ID, String PW){                // Create an account
            try{
                Account e=new Account(ID, PW);
                data.add(e);
                saveAccounts(data);
                return true;
            } catch (Exception ex) {
                System.out.println("-Error creating account:\n");
                ex.printStackTrace();
                return false;
            }
            
        }
	public synchronized void LoadAccounts() {

		// This checks if the file actually exists
                try{
                    if(this.file.exists() && !this.file.isDirectory()) { 

                        ObjectInputStream FILEtoDB = new ObjectInputStream(new FileInputStream(this.file.getName()));
                        data= (ArrayList<Account>) FILEtoDB.readObject();
                        FILEtoDB.close();
                        System.out.println(data.size() + " compte(s) chargé(s) depuis la sauvegarde.\n");                    

                    } else {
                            System.out.println("Le fichier de sauvegarde n'existe pas.\n");
                    }
                } catch(Exception ex){
                    ex.printStackTrace();
                }
	}
	
	
	public synchronized void saveAccounts(ArrayList<Account> data) throws IOException {

            ObjectOutputStream DBtoFILE = new ObjectOutputStream(new FileOutputStream(this.file.getName()));
            DBtoFILE.writeObject(data);
            DBtoFILE.close();

		
		System.out.println("Sauvegarde effectuée... " + data.size() + " comptes enregistrés.");
	}
}
