package pt.iul.ista.pcd.user;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	private ServerSocket ss;
	private User user;

	public Server(int porto, User user) throws IOException {
		this.user = user;
		ss = new ServerSocket(porto);
	}

	public void startServing() {
		System.out.println("ServerSocket ligada! - Port: " + String.valueOf(ss.getLocalPort()));
		try {
			while (true) {
				Socket socket = ss.accept();
				System.out.println("Conectado: " + socket.toString());
				new DealWithClient(socket, user).start();
			}
		} catch (IOException e) {
			System.err.println("Erro desconhecido ao fazer a ligação ao cliente!");
		} finally {
			try {
				ss.close();
			} catch (IOException e) {
				System.err.println("Erro desconhecido ao fechar a ServerSocket");
			}
		}
	}

}
