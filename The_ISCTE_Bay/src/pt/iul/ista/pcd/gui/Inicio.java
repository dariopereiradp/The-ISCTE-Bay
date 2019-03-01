package pt.iul.ista.pcd.gui;

import java.awt.Dimension;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import main.ErrorMessage;
import mdlaf.animation.MaterialUIMovement;
import mdlaf.utils.MaterialColors;
import pt.iul.ista.pcd.user.User;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;

public class Inicio {
	
	private JFrame frame;
	private JTextField tfNome;
	private JTextField tfIP;
	private JTextField tfPD;
	private JTextField tfP;
	private JTextField tfPasta;
	
	public Inicio(){
		frame = new JFrame("Dados do Utilizador");
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage((Toolkit.getDefaultToolkit().getImage((getClass().getResource("isctebay.png")))));
		frame.setLocationByPlatform(true);
		frame.setMinimumSize(new Dimension(300, 400));
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JLabel lblIntroduzOsDados = new JLabel("INTRODUZ OS DADOS");
		lblIntroduzOsDados.setFont(new Font("Roboto", Font.PLAIN, 18));
		lblIntroduzOsDados.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblIntroduzOsDados, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JButton btnIniciar = new JButton("Iniciar");
		MaterialUIMovement.add(btnIniciar, MaterialColors.GRAY_300, 5, 1000 / 30);
		btnIniciar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iniciar();
			}
		});
		btnIniciar.setBounds(195, 315, 89, 23);
		panel.add(btnIniciar);
		
		JLabel lblNome = new JLabel("Nome:");
		lblNome.setFont(new Font("Roboto", Font.PLAIN, 13));
		lblNome.setBackground(Color.WHITE);
		lblNome.setBounds(10, 35, 46, 14);
		panel.add(lblNome);
		
		tfNome = new JTextField();
		tfNome.setBounds(66, 32, 218, 20);
		panel.add(tfNome);
		tfNome.setColumns(10);
		
		JLabel lblIpDoDiretrio = new JLabel("IP do Diret\u00F3rio:");
		lblIpDoDiretrio.setFont(new Font("Roboto", Font.PLAIN, 13));
		lblIpDoDiretrio.setBackground(Color.WHITE);
		lblIpDoDiretrio.setBounds(10, 85, 89, 14);
		panel.add(lblIpDoDiretrio);
		
		tfIP = new JTextField();
		tfIP.setColumns(10);
		tfIP.setBounds(128, 83, 156, 20);
		panel.add(tfIP);
		
		JLabel lblPortoDoDiretrio = new JLabel("Porto do Diret\u00F3rio:");
		lblPortoDoDiretrio.setFont(new Font("Roboto", Font.PLAIN, 13));
		lblPortoDoDiretrio.setBackground(Color.WHITE);
		lblPortoDoDiretrio.setBounds(10, 136, 127, 14);
		panel.add(lblPortoDoDiretrio);
		
		tfPD = new JTextField();
		tfPD.setColumns(10);
		tfPD.setBounds(128, 134, 156, 20);
		panel.add(tfPD);
		
		JLabel lblPortoDeServio = new JLabel("Porto de Servi\u00E7o:");
		lblPortoDeServio.setFont(new Font("Roboto", Font.PLAIN, 13));
		lblPortoDeServio.setBackground(Color.WHITE);
		lblPortoDeServio.setBounds(10, 193, 127, 14);
		panel.add(lblPortoDeServio);
		
		tfP = new JTextField();
		tfP.setColumns(10);
		tfP.setBounds(128, 191, 156, 20);
		panel.add(tfP);
		
		JLabel lblPastaDeFicheiros = new JLabel("Pasta de Ficheiros");
		lblPastaDeFicheiros.setFont(new Font("Roboto", Font.PLAIN, 13));
		lblPastaDeFicheiros.setBackground(Color.WHITE);
		lblPastaDeFicheiros.setBounds(10, 243, 127, 14);
		panel.add(lblPastaDeFicheiros);
		
		tfPasta = new JTextField();
		tfPasta.setColumns(10);
		tfPasta.setBounds(128, 241, 156, 20);
		panel.add(tfPasta);
		
		JButton btnEscolher = new JButton("Escolher");
		MaterialUIMovement.add(btnEscolher, MaterialColors.GRAY_300, 5, 1000 / 30);
		btnEscolher.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tfPasta.setText(getPath());
			}
		});
		btnEscolher.setBounds(158, 265, 104, 23);
		panel.add(btnEscolher);
	}
	
	public void open(){
		frame.setVisible(true);
	}
	
	public void iniciar(){
		ErrorMessage error = new ErrorMessage();
		String message;
		try{
		String nome = tfNome.getText();
		String ip = tfIP.getText();
		int portoDir = Integer.parseInt(tfPD.getText());
		int porto = Integer.parseInt(tfP.getText());
		String pasta = tfPasta.getText();
		User user = new User(nome, ip, portoDir, porto, pasta);
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				createAndShowGui(user);
				
			}
		});
		frame.setVisible(false);
		user.runClient();
		user.startServidor();
		} catch (NumberFormatException e) {
			message = "Um ou mais argumentos inválidos: " + e.getMessage();
			error.errormessage(message);
		} catch (UnknownHostException e) {
			message = "Endereço IP inválido ou desconhecido: " + e.getMessage();
			error.errormessage(message);
		} catch (IOException e) {
			message = "IO Exception: " + e.getMessage();
			error.errormessage(message);
		} catch (ArrayIndexOutOfBoundsException e){
			message = "Número de argumentos insuficiente!";
			error.errormessage(message);
		} catch (NullPointerException e){
			message = "Erro ao criar o utilizador!";
			error.errormessage(message);
		}
		
	}
	
	private static void createAndShowGui(User user){
		Gui gui = new Gui(user);
		user.addObserver(gui);
		gui.open();
	}
	
	public String getPath(){
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfc.showOpenDialog(frame);
		String path = jfc.getSelectedFile().getPath();
		return path;
	}
}
