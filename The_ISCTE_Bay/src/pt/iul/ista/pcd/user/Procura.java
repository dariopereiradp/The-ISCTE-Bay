package pt.iul.ista.pcd.user;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Procura extends Thread {

	private ObjectInputStream in;
	private ObjectOutputStream out;
	private WordSearchMessage keyword;
	private InetAddress ip;
	private int porto;
	private Socket socket;
	private User user;

	public Procura(User user, WordSearchMessage keyword, String ip, int porto) {

		try {
			this.user = user;
			this.keyword = keyword;
			this.ip = InetAddress.getByName(ip);
			this.porto = porto;
		} catch (UnknownHostException e) {
			System.out.println("IP inválido");
		}

	}

	public void connect() throws IOException {
		socket = new Socket(ip, porto);
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(socket.getInputStream());
	}

	@Override
	public void run() {
		try {
			connect();
			out.writeObject(keyword);
			out.reset();
			out.flush();
			Object o = in.readObject();
			if (o instanceof FileDetails) {
				FileDetails fd = ((FileDetails) o);
				user.addFileDetails(fd);
			}
		} catch (IOException e) {
			System.out.println("Erro IOException: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println("Erro: " + e.getMessage());
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("Erro ao fechar a socket " + socket.toString());
			}
		}
	}

	@Override
	public String toString() {
		return "Procura: " + keyword + ", ip=" + ip + ", porto=" + porto + "]";
	}

}
