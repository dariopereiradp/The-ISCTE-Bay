package pt.iul.ista.pcd.dir;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Diretorio extends Observable {

	private static Diretorio INSTANCE;
	private ServerSocket ss;
	private List<Cliente> users = new ArrayList<>();
	private int porto;

	public Diretorio(int porto) throws IOException {
		this.porto = porto;
		INSTANCE = this;
	}

	public void startServing() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				print("Início...");
				
				try {
					ss = new ServerSocket(porto);
					print("IP do Diretorio: " + getIP());
					print(ss.toString());
					while (true) {
						Socket socket = ss.accept();
						new DealWithClient(socket, INSTANCE).start();
					}
				} catch (IOException e) {
					print("ServerSocket encerrada!", true);
				} finally {
					close();
				}
			}
		}).start();
		
	}

	public void close() {
		try {
			ss.close();
		} catch (IOException e) {
			print("Erro desconhecido ao fechar a ServerSocket", true);
		}

	}

	public String getIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			print("Impossível encontrar o IP requerido!", true);
			return "0.0.0.0";
		}
	}

	public int getPorto() {
		return porto;
	}

	public void addCliente(Cliente cliente) {
		users.add(cliente);
		setChanged();
		notifyObservers(cliente);
	}

	public List<Cliente> getUsers() {
		return users;
	}

	public synchronized void removeClient(int porto) {
		Cliente client = null;
		for (Cliente c : users) {
			if (c.getPortoAtribuido() == porto)
				client = c;
		}
		users.remove(client);
		setChanged();
		notifyObservers(porto);
	}

	public String getClientName(int porto) {
		String s = "";
		for (Cliente c : users) {
			if (c.getPortoAtribuido() == porto)
				s = c.getNome();
		}
		return s;
	}

	public synchronized void inscricao(String[] tokens, Socket socket) {
		String nome = tokens[1];
		String endereco = tokens[2];
		int porto = Integer.parseInt(tokens[3]);
		int portoAtribuido = socket.getPort();
		addCliente(new Cliente(nome, endereco, porto, portoAtribuido));
	}

	public synchronized void listarUsers(PrintWriter out) {
		for (Cliente c : getUsers()) {
			out.println(c.toText());
		}
		out.println("END");
	}

	public void print(String message, boolean err) {
		if (err)
			System.err.println(message);
		else
			System.out.println(message);
		setChanged();
		notifyObservers(message);
	}

	public void print(String message) {
		System.out.println(message);
		setChanged();
		notifyObservers(message);
	}
}
