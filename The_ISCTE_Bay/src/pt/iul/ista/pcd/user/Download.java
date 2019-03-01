package pt.iul.ista.pcd.user;

import static java.lang.Math.toIntExact;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Observable;

import javax.swing.JOptionPane;

public class Download extends Observable {

	public static final int TAMANHO_PARTE = 1024;
	private FileInfo file;
	private User user;
	byte[] fileContents;
	int tamanho;
	int bytesOK = 0;

	public Download(User user, FileInfo file) {
		this.user = user;
		this.file = file;
	}

	public User getUser() {
		return user;
	}

	public int getBytesOK() {
		return bytesOK;
	}

	public int getTamanho() {
		return tamanho;
	}

	public void iniciar() throws IOException {

		new Thread(new Runnable() {

			@Override
			public void run() {

				startDownload();
			}
		}).start();
	}

	public void startDownload() {
		try {
			tamanho = toIntExact(file.getTamanho());
			System.out.println("Tamanho: " + tamanho);
			int numero_partes = tamanho / TAMANHO_PARTE;
			int bytesRestantes = tamanho % TAMANHO_PARTE;
			System.out.println("Tamanho: " + tamanho + " | Número de partes: " + numero_partes + " | BytesRestantes: "
					+ bytesRestantes);
			fileContents = new byte[tamanho];
			ArrayList<Proprietario> prop = file.getProprietarios();
			DownloadPartes[] downloadThread = new DownloadPartes[prop.size()];
			ArrayList<FileBlockRequestMessage> blocos = new ArrayList<>();
			int offset = 0;
			for (int i = 0; i < numero_partes; i++) {
				blocos.add(new FileBlockRequestMessage(file, offset, TAMANHO_PARTE));
				offset += TAMANHO_PARTE;
			}
			if (bytesRestantes != 0)
				blocos.add(new FileBlockRequestMessage(file, offset, bytesRestantes));

			BlockingQueue<FileBlockRequestMessage> partes = new BlockingQueue<>(blocos);

			for (int i = 0; i < prop.size(); i++) {
				downloadThread[i] = new DownloadPartes(this, partes, prop.get(i));
			}

			long time = System.currentTimeMillis();

			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						System.out.println("Estou a espera para gravar!");
						esperaTerminar();
						System.out.println("Vou gravar!");
						Files.write(Paths.get(user.getPasta() + "/" + file.getNome()), fileContents);
						long tempo = (System.currentTimeMillis() - time) / 1000;
						setChanged();
						notifyObservers(new Boolean(true));
						System.out.println("Salvo com sucesso!");
						String info = "Descarga completa.\n";
						for (int i = 0; i < prop.size(); i++) {
							info += prop.get(i).toString() + ": " + prop.get(i).getPartes() + "\n";
						}
						info += "Tempo decorrido: " + tempo + "s";
						JOptionPane.showMessageDialog(null, info, file.getNome(), JOptionPane.INFORMATION_MESSAGE);

					} catch (IOException e) {
						System.out.println("Erro ao salvar o ficheiro: " + e.getMessage());
					} catch (InterruptedException e) {
						System.out.println("Erro ao fazer o download do ficheiro: " + e.getMessage());
					}

				}
			}).start();

			for (int i = 0; i < prop.size(); i++) {
				downloadThread[i].start();
			}
		} catch (ArithmeticException e) {
			System.out.println("Ficheiro muito grande! Impossível fazer o download!");
		}
	}

	public synchronized void esperaTerminar() throws InterruptedException {
		while (bytesOK != tamanho) {
			wait();
		}
		notifyAll();
	}

	public synchronized void addPart(FilePart filePart) {
		System.arraycopy(filePart.getPart(), 0, fileContents, filePart.getInicio(), filePart.getTamanho());
		bytesOK += filePart.getTamanho();
		setChanged();
		notifyObservers(new Integer(bytesOK));
		System.out.println("BytesOK: " + bytesOK);
		System.out.println(bytesOK == tamanho);
		if (bytesOK == tamanho)
			notifyAll();
	}

}
