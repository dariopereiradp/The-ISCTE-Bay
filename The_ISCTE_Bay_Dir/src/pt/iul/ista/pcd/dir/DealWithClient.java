package pt.iul.ista.pcd.dir;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JOptionPane;

public class DealWithClient extends Thread {

	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private Diretorio dir;

	public DealWithClient(Socket socket, Diretorio dir) {
		super();
		this.socket = socket;
		this.dir = dir;
	}

	@Override
	public void run() {
		try {
			doConnections();
			serve();
		} catch (IOException e) {
			if (e instanceof SocketException) {
				int port = socket.getPort();
				dir.print("Utilizador " + dir.getClientName(port) + " saiu", true);
				dir.removeClient(port);
			}

		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				dir.print("Erro desconhecido ao fechar o socket", true);
			}
		}

	}

	private void doConnections() throws IOException {
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
	}

	private void serve() throws IOException {
		while (true) {
			String str = in.readLine();
			String[] tokens = dividir(str);
			switch (tokens[0]) {
			case "INSC":
				dir.inscricao(tokens, socket);
				dir.print("Conexão aceite: NOME: " + dir.getClientName(socket.getPort()) + " - " + socket);
				break;
			case "CLT":
				dir.listarUsers(out);
				dir.print("CLT recebido... Enviando lista de clientes para o cliente: "
						+ dir.getClientName(socket.getPort()));
				break;
			default:
				JOptionPane.showMessageDialog(null, "COMANDO INVÁLIDO");
				break;
			}
		}
	}

	public String[] dividir(String instrucao) {
		return instrucao.split(" ");
	}
}
