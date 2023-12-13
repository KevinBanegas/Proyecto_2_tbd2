/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto_2_tbd2;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Job implements Runnable {

    private javax.swing.JTextArea ja;

    public Job(javax.swing.JTextArea ja1) {
        ja = ja1;
    }

    public Job() {
    }

    @Override
    public void run() {
        try {
            while (true) {
                Date d = new Date();
                ja.setText("Ultima Ejecución: " + d.toString());
                Thread.sleep(60000);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Job.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
