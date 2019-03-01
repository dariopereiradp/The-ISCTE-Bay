package pt.iul.ista.pcd.user;

import java.io.Serializable;

public class FileBlockRequestMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2245050039338889144L;
	private FileInfo file;
	private int offset;
	private int lenght;

	public FileBlockRequestMessage(FileInfo file, int offset, int lenght) {
		this.file = file;
		this.offset = offset;
		this.lenght = lenght;
	}

	public FileInfo getFile() {
		return file;
	}

	public int getOffset() {
		return offset;
	}

	public int getLenght() {
		return lenght;
	}

	@Override
	public String toString() {
		return "Block [" + file.getNome() + ", " + offset + "-" + offset + lenght + "]";
	}
}
