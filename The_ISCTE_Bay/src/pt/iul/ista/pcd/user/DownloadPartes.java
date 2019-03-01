package pt.iul.ista.pcd.user;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

public class DownloadPartes extends Thread {

	private static final int TIMEOUT = 10000;
	private Download download;
	private BlockingQueue<FileBlockRequestMessage> partes;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Proprietario prop;
	private InetAddress ip;
	private int porto;
	private Socket socket;

	public DownloadPartes(Download download, BlockingQueue<FileBlockRequestMessage> partes, Proprietario p) {
		try {
			this.download = download;
			this.partes = partes;
			this.prop = p;
			this.ip = InetAddress.getByName(p.getEndereco());
			this.porto = p.getPorto();
		} catch (UnknownHostException e) {
			System.out.println("IP Inválido");
		}

	}

	public void connect() throws IOException {
		socket = new Socket(ip, porto);
		socket.setSoTimeout(TIMEOUT);
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(socket.getInputStream());
	}

	@Override
	public void run() {
		Object parte = null;
		try {
			connect();
			while (!partes.isEmpty() || download.getBytesOK()!=download.getTamanho()) {
				parte = partes.poll();
				out.writeObject(parte);
				out.reset();
				out.flush();
				Object o = in.readObject();

				if (o instanceof FilePart) {
					FilePart filePart = (FilePart) o;
					download.addPart(filePart);
					prop.addParte();
				}
				System.out.println("Download (Fornecedor: " + prop.toString() + prop.getPartes() + " partes)");
			}
		} catch (IOException e) {
			System.err.println("Erro IOException: " + e.getMessage());
			if (e instanceof SocketTimeoutException) {
				JOptionPane.showMessageDialog(null, "O cliente demorou demasiado a responder!", "ERRO",
						JOptionPane.WARNING_MESSAGE);
				try {
					partes.offer((FileBlockRequestMessage) parte);
				} catch (InterruptedException e1) {
					System.out.println("Erro! " + e.getMessage());
					e1.printStackTrace();
				}
			}
			if (e instanceof SocketException) {
				JOptionPane.showMessageDialog(null, "Um cliente que fornecia o ficheiro foi desconectado!", "ERRO",
						JOptionPane.WARNING_MESSAGE);
				try {
					partes.offer((FileBlockRequestMessage) parte);
				} catch (InterruptedException e1) {
					System.out.println("Erro! " + e.getMessage());
					e1.printStackTrace();
				}
			}
		} catch (ClassNotFoundException e) {
			System.out.println("Erro: " + e.getMessage());
		} finally {
			try {
				socket.close();
			} catch (IOException | NullPointerException e) {
				JOptionPane.showMessageDialog(null, "O  utilizador que ia fornecer o ficheiro foi desconectado!",
						"ERRO", JOptionPane.WARNING_MESSAGE);
				System.out.println("Erro ao fechar a socket!");
			}
		}
	}
}
