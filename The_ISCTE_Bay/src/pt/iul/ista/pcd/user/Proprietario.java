package pt.iul.ista.pcd.user;

import java.io.Serializable;

public class Proprietario implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4087137822470331508L;
	private int porto;
	private String endereco;
	private int partes;

	public Proprietario(int porto, String endereco) {
		this.porto = porto;
		this.endereco = endereco;
	}

	public int getPorto() {
		return porto;
	}

	public String getEndereco() {
		return endereco;
	}

	public int getPartes() {
		return partes;
	}

	public void addParte() {
		partes++;
	}

	@Override
	public String toString() {
		return "Fornecedor [Enderço = " + endereco + ", Porto = " + porto + "]";
	}

}
