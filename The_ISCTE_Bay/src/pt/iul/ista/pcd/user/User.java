package pt.iul.ista.pcd.user;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import javax.swing.JOptionPane;

public class User extends Observable {

	public static final int NUM_THREADS = 5;
	private BufferedReader in;
	private PrintWriter out;
	private Socket socket;
	private String nome;
	private InetAddress enderecoPorto;
	private String address;
	private int portoDir;
	private int porto;
	private String pasta;
	private List<String> users = new ArrayList<>();
	private Server servidor;
	private List<FileDetails> fileDetails = new ArrayList<>();
	private String keyword;

	public User(String nome, String endereco, int portoDir, int porto, String pasta) throws UnknownHostException {
		this.nome = nome;
		this.enderecoPorto = InetAddress.getByName(endereco);
		this.portoDir = portoDir;
		this.porto = porto;
		this.pasta = pasta;
	}

	public void runClient() throws IOException {
		connectToDiretorio();
	}

	public void sendMessages(String keyword) {
		out.println("CLT");
		this.keyword = keyword;
	}

	public void startServidor() throws IOException {
		servidor = new Server(porto, this);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				servidor.startServing();
				
			}
		}).start();
	}

	private void connectToDiretorio() throws IOException {
		address = InetAddress.getLocalHost().getHostAddress();
		socket = new Socket(enderecoPorto, portoDir);

		System.out.println(getName() + " ligado ao Diretório! - " + socket);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		String insc = "INSC " + nome + " " + address + " " + porto;
		out.println(insc);
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						String s = in.readLine();
						System.out.println(s);
						if (!s.equals("END"))
							users.add(s);
						else
							procuraServer();
					} catch (IOException e) {
						System.err.println("O diretório foi desconectado! Conexão encerrada!");
						JOptionPane.showMessageDialog(null, "O diretório foi desconectado! Conexão encerrada!",
								"The ISCTE Bay", JOptionPane.ERROR_MESSAGE);
						System.exit(1);
					}
				}
			}
		}).start();

	}

	public String getIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			System.err.println("Impossível encontrar o IP requerido!");
			return "0.0.0.0";
		}
	}

	public String getPasta() {
		return pasta;
	}

	public String getName() {
		return nome;
	}

	public void procuraServer() {
		if (users.size() == 1)
			JOptionPane.showMessageDialog(null, "Nenhum utilizador conectado", "UTILIZADORES OFFLINE",
					JOptionPane.WARNING_MESSAGE);
		else {
			WordSearchMessage wsm = new WordSearchMessage(keyword);
			ArrayList<Procura> procuras = new ArrayList<>();
			for (String s : users) {
				String[] tokens = s.split(" ");
				String ip = tokens[1];
				int porto = Integer.parseInt(tokens[2]);
				if (!(ip.equals(address) && porto == this.porto))
					procuras.add(new Procura(this, wsm, ip, porto));
			}
			for (Procura p : procuras) {
				p.start();
			}
			for (Procura p : procuras) {
				try {
					p.join();
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
				}
			}
			atualizarLista();
		}
		users.clear();
	}

	public FileDetails procuraClient(WordSearchMessage wsm) {
		ArrayList<FileInfo> details = new ArrayList<>();
		String keyword = wsm.getKeyword();
		File[] files = new File(pasta).listFiles();
		for (int i = 0; i < files.length; i++) {
			String name = files[i].getName();
			if (name.toLowerCase().contains(keyword.toLowerCase())) {
				details.add(new FileInfo(name, files[i].length()));
			}
		}
		FileDetails fd = new FileDetails(details, new Proprietario(porto, getIP()));
		return fd;
	}

	public synchronized void addFileDetails(FileDetails fd) {
		fileDetails.add(fd);
	}

	public boolean existsFile(ArrayList<FileInfo> files, FileInfo s) {
		boolean existe = false;
		for (FileInfo file : files) {
			if (s.equals(file))
				existe = true;
		}
		if (!existe) {
			existe = existsFileHere(s);
		}
		return existe;
	}

	public boolean existsFileHere(FileInfo file) {
		boolean existe = false;
		File[] filesHere = new File(pasta).listFiles();
		for (int i = 0; i < filesHere.length; i++) {
			if (file.equals(new FileInfo(filesHere[i].getName(), filesHere[i].length())))
				existe = true;
		}
		return existe;
	}

	public void addProprietario(ArrayList<FileInfo> fileNames, FileInfo f, Proprietario p) {
		for (FileInfo file : fileNames) {
			if (f.equals(file)) {
				file.addProprietario(p);
				break;
			}
		}
	}

	public void atualizarLista() {
		ArrayList<FileInfo> fileNames = new ArrayList<>();

		for (int i = 0; i < fileDetails.size(); i++) {
			ArrayList<FileInfo> tmp = fileDetails.get(i).getDetails();
			for (int j = 0; j < tmp.size(); j++) {
				FileInfo f = tmp.get(j);
				Proprietario p = fileDetails.get(i).getProprietario();
				f.addProprietario(p);
				if (!existsFile(fileNames, tmp.get(j)))
					fileNames.add(tmp.get(j));
				else
					addProprietario(fileNames, f, p);

			}
		}
		setChanged();
		notifyObservers(fileNames);
		fileDetails.clear();
	}

}
