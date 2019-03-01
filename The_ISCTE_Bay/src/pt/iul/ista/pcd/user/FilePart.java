package pt.iul.ista.pcd.user;

import java.io.Serializable;

public class FilePart implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6827411300678280219L;
	private byte[] part;
	private int inicio;
	private int tamanho;

	public FilePart(byte[] part, int inicio, int tamanho) {
		this.part = part;
		this.inicio = inicio;
		this.tamanho = tamanho;
	}

	public byte[] getPart() {
		return part;
	}

	public int getInicio() {
		return inicio;
	}

	public int getTamanho() {
		return tamanho;
	}
}
