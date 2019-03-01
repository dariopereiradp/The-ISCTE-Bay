package pt.iul.ista.pcd.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import pt.iul.ista.pcd.dir.Cliente;
import pt.iul.ista.pcd.dir.Diretorio;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.awt.event.ActionEvent;
import javax.swing.JSeparator;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Font;

public class Gui implements Observer {

	private JFrame frame;
	private DefaultListModel<Cliente> listModel = new DefaultListModel<>();
	private JList<Cliente> conexoes = new JList<>(listModel);
	private JScrollPane scrollCon = new JScrollPane(conexoes);
	private JTextArea log = new JTextArea();
	private JScrollPane scrollLog = new JScrollPane(log);
	private JButton iniciar;
	private JButton parar;
	private final JSeparator separator = new JSeparator();
	private final JLabel lblClientes = new JLabel("CLIENTES                ");
	private final JLabel lblLog = new JLabel("                             LOG");
	private boolean iniciou = false;

	public Gui(Diretorio dir) {
		frame = new JFrame("DIRETÓRIO");
		frame.setIconImage((Toolkit.getDefaultToolkit().getImage((getClass().getResource("isctebay.png")))));
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setMinimumSize(new Dimension(800, 300));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		scrollCon.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		log.setEditable(false);
		log.setMinimumSize(new Dimension(300, 200));
		conexoes.setMinimumSize(new Dimension(300, 200));
		JPanel botoes = new JPanel();
		JPanel lista = new JPanel(new GridLayout(1, 2));
		lista.add(scrollCon);
		lista.add(scrollLog);

		iniciar = new JButton("Iniciar");
		iniciar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (iniciou) {
					JOptionPane.showMessageDialog(frame, "Diretório já inciado!", "Iniciar",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					dir.startServing();
					iniciou = true;
				}
			}
		});
		parar = new JButton("Parar");
		parar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (iniciou) {
					int opcao = JOptionPane.showConfirmDialog(frame,
							"Vai desligar a conexão.\nTem a certeza?", "Sair",
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
					if (opcao == JOptionPane.OK_OPTION) {
						dir.close();
						iniciou = false;
					}
				}
			}
		});
		lblClientes.setFont(new Font("Roboto", Font.PLAIN, 14));
		lblClientes.setHorizontalAlignment(SwingConstants.LEFT);

		botoes.add(lblClientes);

		botoes.add(iniciar);
		botoes.add(parar);
		frame.getContentPane().add(botoes, BorderLayout.NORTH);
		lblLog.setHorizontalAlignment(SwingConstants.RIGHT);
		lblLog.setFont(new Font("Roboto", Font.PLAIN, 14));

		botoes.add(lblLog);
		frame.getContentPane().add(lista, BorderLayout.CENTER);
		scrollLog.setRowHeaderView(separator);

		frame.setVisible(true);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 instanceof String) {
			if (log.getText().equals(""))
				log.append((String) arg1);
			else
				log.append("\n" + (String) arg1);
		} else if (arg1 instanceof Cliente)
			listModel.addElement((Cliente) arg1);
		else if (arg1 instanceof Integer) {
			removeClient((Integer) arg1);
		}

	}

	public synchronized void removeClient(int porto) {
		int index = 0;
		for (int i = 0; i < listModel.getSize(); i++) {
			Cliente c = listModel.getElementAt(i);
			if (c.getPortoAtribuido() == porto) {
				index = i;
				break;
			}
		}
		listModel.remove(index);
	}

}
