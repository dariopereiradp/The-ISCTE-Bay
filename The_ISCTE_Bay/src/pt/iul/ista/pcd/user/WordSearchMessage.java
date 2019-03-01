package pt.iul.ista.pcd.user;

import java.io.Serializable;

public class WordSearchMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2098300923294325844L;
	private String keyword;

	public WordSearchMessage(String keyword) {
		this.keyword = keyword;
	}

	public String getKeyword() {
		return keyword;
	}

	@Override
	public String toString() {
		return "Palavra: " + keyword;
	}

}
