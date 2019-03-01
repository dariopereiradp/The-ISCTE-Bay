package pt.iul.ista.pcd.dir;

public class Cliente {

	private String nome;
	private String endereco;
	private int porto;
	private int portoAtribuido;

	public Cliente(String nome, String endereco, int porto, int portoAtribuido) {
		this.nome = nome;
		this.endereco = endereco;
		this.porto = porto;
		this.portoAtribuido = portoAtribuido;
	}

	public String getNome() {
		return nome;
	}

	public String getEndereco() {
		return endereco;
	}

	public int getPorto() {
		return porto;
	}

	public int getPortoAtribuido() {
		return portoAtribuido;
	}
	
	@Override
	public String toString(){
		return endereco + " - " + porto + " - " + nome + " - Porto Atribuído: " + portoAtribuido;
	}
	
	public String toText() {
		return "CLT " + endereco + " " + porto;
	}
}
