/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.Serializable;

/**
 *
 * @author labon
 */
public class Account implements Serializable {
    protected String ID;
    protected String PW;

    public Account(String ID, String PW) { // Appelé par Account database
        this.ID = ID;
        this.PW = PW;
    }

    public String getPW() {
        return PW;
    }

    public String getID() {
        return ID;
    }
    
    
    
}
