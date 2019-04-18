package main;

import javax.swing.JOptionPane;

/**
 * 
 * @author Dário Pereira
 *
 */
public class ErrorMessage {
	
	public void errormessage(String message) {
		JOptionPane.showMessageDialog(null, message, "The ISCTE Bay", JOptionPane.ERROR_MESSAGE);
		System.err.println(message);
		System.exit(1);
	}

}
