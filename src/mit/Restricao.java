package mit;

import java.util.Comparator;

public class Restricao {
	private int indice;
	private String simboloRestricao;
	private int xInteiro;

	public int getIndice() {
		return indice;
	}

	public void setIndice(int indice) {
		this.indice = indice;
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

	public Restricao(int indice, String simboloRestricao, double xFracionado) {
		this.indice = indice;
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
		if (this.indice == novaRestricao.indice && this.simboloRestricao.equals(">=")
				&& novaRestricao.simboloRestricao.equals("<=") && this.xInteiro == novaRestricao.xInteiro) {
			return true;
		}
		if (this.indice == novaRestricao.indice && this.simboloRestricao.equals("<=")
				&& novaRestricao.simboloRestricao.equals(">=") && this.xInteiro == novaRestricao.xInteiro) {
			return true;
		}
		return false;
	}

	public boolean ehMaisRestritiva(Restricao novaRestricao) {
		// caso em que a restrição já existente tem o mesmo label, o mesmo símbolo e é e
		// pe
		if (this.indice == novaRestricao.indice && this.simboloRestricao.equals(novaRestricao.simboloRestricao)) {
			if (this.simboloRestricao.equals("<=")) {
				if (this.xInteiro <= novaRestricao.xInteiro) {
					return true;

				}
			} else if (this.simboloRestricao.equals(">=")) {
				if (this.xInteiro >= novaRestricao.xInteiro) {
					return true;
				}
			}
		}
		return false;
	}

	public static Comparator<Restricao> porIndice() {
		return new Comparator<Restricao>() {
			public int compare(Restricao r1, Restricao r2) {
				return r1.indice - r2.indice;
			}
		};
	}
}