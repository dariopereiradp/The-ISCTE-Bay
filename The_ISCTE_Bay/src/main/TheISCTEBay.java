package main;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import mdlaf.MaterialLookAndFeel;
import pt.iul.ista.pcd.gui.Inicio;

public class TheISCTEBay {

	public static void main(String[] args) {
		ErrorMessage error = new ErrorMessage();
		String message;
		try {
			UIManager.setLookAndFeel(new MaterialLookAndFeel());
			new Inicio().open();
		} catch (UnsupportedLookAndFeelException e) {
			message = "Erro no Look and Feel";
			error.errormessage(message);
		}

	}
}
