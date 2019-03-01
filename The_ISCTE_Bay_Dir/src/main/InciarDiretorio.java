package main;

import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import pt.iul.ista.pcd.dir.Diretorio;
import pt.iul.ista.pcd.gui.Gui;

public class InciarDiretorio {
	
	private static void createAndShowGui(Diretorio dir){
		Gui gui = new Gui(dir);
		dir.addObserver(gui);
	}

	public static void main(String[] args) {
		ErrorMessage error = new ErrorMessage();
		String message;
		try {
			int port = Integer.parseInt(JOptionPane.showInputDialog(null,
					"Indique o porto em que o servidor ir� operar: ", "PORTO", JOptionPane.QUESTION_MESSAGE));
			Diretorio dir = new Diretorio(port);
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					createAndShowGui(dir);
					
				}
			});
		} catch (NumberFormatException e) {
			message = "Porto inv�lido!";
			error.errormessage(message);
		} catch (IOException e) {
			message = "Imposs�vel iniciar o diret�rio: IP e/ou porto j� est� em uso!";
			error.errormessage(message);
		} catch (ArrayIndexOutOfBoundsException e) {
			message = "N�mero de argumentos insuficiente!";
			error.errormessage(message);
		} 

	}

}
