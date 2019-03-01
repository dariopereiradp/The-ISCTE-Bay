package pt.iul.ista.pcd.user;

import java.io.Serializable;
import java.util.ArrayList;

public class FileDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4853812688342108996L;
	private ArrayList<FileInfo> details;
	private Proprietario info;

	public FileDetails(ArrayList<FileInfo> details, Proprietario info) {
		this.details = details;
		this.info = info;
	}

	public Proprietario getProprietario() {
		return info;
	}

	public ArrayList<FileInfo> getDetails() {
		return details;
	}

	public boolean exists(FileInfo fi) {
		for (FileInfo f : details) {
			if (f.equals(fi))
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "FileDetails: " + details;
	}
}
