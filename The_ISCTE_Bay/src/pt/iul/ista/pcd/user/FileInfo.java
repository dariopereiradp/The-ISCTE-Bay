package pt.iul.ista.pcd.user;

import java.io.Serializable;
import java.util.ArrayList;

public class FileInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1718041648682806469L;
	private String nome;
	private long tamanho;
	private ArrayList<Proprietario> proprietarios = new ArrayList<>();

	public FileInfo(String nome, long tamanho) {
		this.nome = nome;
		this.tamanho = tamanho;
	}

	public String getNome() {
		return nome;
	}

	public long getTamanho() {
		return tamanho;
	}

	public ArrayList<Proprietario> getProprietarios() {
		return proprietarios;
	}

	public void addProprietario(Proprietario p) {
		proprietarios.add(p);
	}

	@Override
	public String toString() {
		return "(" + nome + ", " + tamanho + " bytes)";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime * result + (int) (tamanho ^ (tamanho >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileInfo other = (FileInfo) obj;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		if (tamanho != other.tamanho)
			return false;
		return true;
	}

}
