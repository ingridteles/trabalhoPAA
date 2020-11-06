package mit;

import java.util.Comparator;

public class Restricao {
	private int label;
	private String simboloRestricao;
	private int xInteiro;

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}

	public String getSimboloRestricao() {
		return simboloRestricao;
	}

	public void setSimboloRestricao(String simboloRestricao) {
		this.simboloRestricao = simboloRestricao;
	}

	public double getXinteiro() {
		return xInteiro;
	}

	public void setXInteiro(int xInteiro) {
		this.xInteiro = xInteiro;
	}

	public Restricao(int label, String simboloRestricao, double xFracionado) {
		this.label = label;
		this.simboloRestricao = simboloRestricao;
		if (simboloRestricao.equals(">=")) {
			this.xInteiro = (int) xFracionado + 1;
		} else if (simboloRestricao.equals("<=")) {
			this.xInteiro = (int) xFracionado;
		} else if (simboloRestricao.equals("==")) {
			this.xInteiro = (int) xFracionado;
		}
	}

	public boolean ehRestricaoOposta(Restricao novaRestricao) {
		if (this.label == novaRestricao.label && this.simboloRestricao.equals(">=")
				&& novaRestricao.simboloRestricao.equals("<=") && this.xInteiro == novaRestricao.xInteiro) {
			return true;
		}
		if (this.label == novaRestricao.label && this.simboloRestricao.equals("<=")
				&& novaRestricao.simboloRestricao.equals(">=") && this.xInteiro == novaRestricao.xInteiro) {
			return true;
		}
		return false;
	}

	public static Comparator<Restricao> porLabel() {
		return new Comparator<Restricao>() {
			public int compare(Restricao r1, Restricao r2) {
				return r1.label - r2.label;
			}
		};
	}
}