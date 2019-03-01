package pt.iul.ista.pcd.user;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;

public class DealWithClient extends Thread {

	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private User user;
	private ThreadPool pool = new ThreadPool(User.NUM_THREADS);

	public DealWithClient(Socket socket, User user) {
		super();
		this.socket = socket;
		this.user = user;
	}

	@Override
	public void run() {
		try {

			doConnections();
			serve();
		} catch (IOException e) {
			if (e instanceof SocketException) {
				System.err.println("Cliente saiu");
			}

		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				System.err.println("Erro desconhecido ao fechar o socket");
			}
		}

	}

	private void doConnections() throws IOException {
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(socket.getInputStream());
	}

	private void serve() throws IOException {
		while (true) {
			try {
				Object o = in.readObject();
				if (o instanceof WordSearchMessage) {
					FileDetails fd = user.procuraClient((WordSearchMessage) o);
					System.out.println(fd);
					out.writeObject(fd);
					out.reset();
					out.flush();
				}
				if (o instanceof FileDetails) {
					FileDetails fd = ((FileDetails) o);
					user.addFileDetails(fd);
				}
				if (o instanceof FileBlockRequestMessage)
					pool.submit(new EnviaParte((FileBlockRequestMessage) o));
			} catch (ClassNotFoundException e) {
				System.err.println("Erro: Classe não encontrada - " + e.getMessage());
			}
		}
	}

	private class EnviaParte implements Runnable {

		private FileBlockRequestMessage fileBlock;

		private EnviaParte(FileBlockRequestMessage fileBlock) {
			this.fileBlock = fileBlock;
		}

		@Override
		public void run() {
			if (user.existsFileHere(fileBlock.getFile())) {
				File file = null;
				File[] filesHere = new File(user.getPasta()).listFiles();
				for (int i = 0; i < filesHere.length; i++) {
					if (fileBlock.getFile().equals(new FileInfo(filesHere[i].getName(), filesHere[i].length()))) {
						file = filesHere[i];
						break;
					}
				}
				System.out.println(user.getName() + " enviei mais uma parte do ficheiro: " + fileBlock.getFile().getNome());
				try {
					byte[] fileContents = Files.readAllBytes(file.toPath());
					byte[] part = new byte[fileBlock.getLenght()];
					System.arraycopy(fileContents, fileBlock.getOffset(), part, 0, fileBlock.getLenght());
					synchronized (out) {
						out.writeObject(new FilePart(part, fileBlock.getOffset(), fileBlock.getLenght()));
					}
				} catch (IOException e) {
					System.err.println("Erro IOException: " + e.getMessage());
				}
			} else
				System.err.println("Erro no download: Ficheiro pedido já não existe!");
		}
	}
}
