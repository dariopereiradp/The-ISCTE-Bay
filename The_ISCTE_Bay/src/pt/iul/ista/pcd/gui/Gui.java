package pt.iul.ista.pcd.gui;

import static java.lang.Math.toIntExact;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import mdlaf.animation.MaterialUIMovement;
import mdlaf.utils.MaterialColors;
import pt.iul.ista.pcd.user.Download;
import pt.iul.ista.pcd.user.FileInfo;
import pt.iul.ista.pcd.user.User;

public class Gui implements Observer {

	private static final String TITLE = "The ISCTE Bay";
	private JFrame frame;
	private JPanel north;
	private JPanel right;
	private JPanel principal;
	private JLabel img;
	private JTextField tProcura;
	private JLabel textoAprocurar;
	private JButton procurar;
	private JButton descarregar;
	private JProgressBar progresso;
	private DefaultListModel<FileInfo> listaFilesModel = new DefaultListModel<>();
	private JList<FileInfo> listaFiles = new JList<>(listaFilesModel);
	private User user;

	private void carregar() {
		frameInic();
		elementosInic();
		painelInic();
		botoesAction();
	}

	public void frameInic() {
		frame = new JFrame(TITLE + " - " + user.getName());
		frame.setIconImage((Toolkit.getDefaultToolkit().getImage((getClass().getResource("isctebay.png")))));
		frame.setSize(516, 566);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new GridLayout(2, 1));
	}

	public void elementosInic() {
		tProcura = new JTextField();
		textoAprocurar = new JLabel("Texto a proucurar: ");
		procurar = new JButton("Procurar");
		MaterialUIMovement.add(procurar, MaterialColors.GRAY_300, 5, 1000 / 30);
		descarregar = new JButton("Descarregar");
		MaterialUIMovement.add(descarregar, MaterialColors.GRAY_300, 5, 1000 / 30);
		progresso = new JProgressBar();
		progresso.setStringPainted(true);
		img = new JLabel(new ImageIcon(getClass().getResource("the_iscte_bay.png")));
	}

	public void painelInic() {
		north = new JPanel(new GridLayout(1, 3));
		right = new JPanel(new GridLayout(2, 1));
		principal = new JPanel(new BorderLayout());
		north.add(textoAprocurar);
		north.add(tProcura);
		north.add(procurar);
		right.add(descarregar);
		right.add(progresso);
		principal.add(north, BorderLayout.NORTH);
		principal.add(right, BorderLayout.EAST);
		listaFiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		principal.add(new JScrollPane(listaFiles), BorderLayout.CENTER);
		frame.add(img);
		frame.add(principal);
	}

	public void botoesAction() {
		procurar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				progresso.setValue(0);
				if ("".equals(tProcura.getText().trim()))
					JOptionPane.showMessageDialog(frame, "Escreve uma palavra", "PALAVRA VAZIA",
							JOptionPane.WARNING_MESSAGE);
				else
					user.sendMessages(tProcura.getText());
			}
		});
		descarregar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (listaFiles.getSelectedIndex() == -1)
					JOptionPane.showMessageDialog(frame, "Nenhum ficheiro selecionado", "NENHUM FICHEIRO",
							JOptionPane.WARNING_MESSAGE);
				else {
					FileInfo file = listaFiles.getSelectedValue();
					descarregar(file);
				}

			}
		});
	}

	public void descarregar(FileInfo file) {
		try {
			Download download = new Download(user, file);
			download.addObserver(this);
			int tamanho = toIntExact(file.getTamanho());
			progresso.setValue(0);
			progresso.setString(null);
			progresso.setMinimum(0);
			progresso.setMaximum(tamanho);
			download.iniciar();
		} catch (ArithmeticException e1) {
			JOptionPane.showMessageDialog(frame, "Ficheiro demasiado longo", "FICHEIRO GRANDE",
					JOptionPane.WARNING_MESSAGE);
		} catch (IOException e2) {
			JOptionPane.showMessageDialog(frame, "Erro ao fazer download do ficheiro: " + e2.getMessage(),
					"ERRO NO DOWNLOAD", JOptionPane.ERROR_MESSAGE);
		}

	}

	public void open() {
		frame.setVisible(true);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 instanceof ArrayList<?>) {
			@SuppressWarnings("unchecked")
			ArrayList<FileInfo> fileNames = (ArrayList<FileInfo>) arg1;
			addElements(fileNames);

		} else if (arg1 instanceof Boolean) {
			boolean b = (Boolean) arg1;
			if (b == true)
				progresso.setString("100% - CONCLUIDO");
		} else if (arg1 instanceof Integer) {
			int value = (Integer) arg1;
			progresso.setValue(value);
		}
	}

	public void addElements(ArrayList<FileInfo> fileNames) {
		if (fileNames.isEmpty())
			JOptionPane.showMessageDialog(frame, "Nenhum ficheiro encontrado", "NENHUM FICHEIRO",
					JOptionPane.WARNING_MESSAGE);
		listaFilesModel.clear();
		for (FileInfo file : fileNames)
			listaFilesModel.addElement(file);

	}

	public Gui(User user) {
		this.user = user;
		carregar();
	}
}
