/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SHARE;

/**
 *
 * @author labon
 */
import java.io.IOException;

public class CLS {
    public void clearScreen() throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }
}
