package mit;

import java.util.Comparator;

public class Item {

	private int label;
	private double valor;
	private double d1;
	private double d2;
	private double d3;
	private double x;

	public Item(Item item, double solucao) {
		this.label = item.label;
		this.valor = item.valor;
		this.d1  = item.d1;
		this.d2 = item.d2;
		this.d3 = item.d3;
		this.x = solucao;
	}

	public Item(int i, double valor, double d1, double d2, double d3) {
		this.label = i;
		this.valor = valor;
		this.d1  = d1;
		this.d2 = d2;
		this.d3 = d3;
		this.x = 0.0;
	}

	public static Comparator<Item> porLabel() {
		return new Comparator<Item>() {
			public int compare(Item i1, Item i2) {
				return i1.label - i2.label;
			}
		};
	}
	
	public static Comparator<Item> porValorDaVariavel() {
		return new Comparator<Item>() {
			public int compare(Item i1, Item i2) {
		        if (i1.x > i2.x) return -1;
		        if (i1.x < i2.x) return 1;
		        return 0;
		    } 
		};
	}
	
	// TODO ajeitar esssa ordenação
	public static Comparator<Item> porCustoBeneficio() {
		return new Comparator<Item>() {
			public int compare(Item item1, Item item2) {
				double custoItem1 = item1.d1*item1.d2*item1.d3;
				double custoBeneficioItem1 = item1.valor/custoItem1;
				
				double custoItem2 = item2.d1*item2.d2*item2.d3;
				double custoBeneficioItem2 = item2.valor/custoItem2;
						
				if (custoBeneficioItem1 > custoBeneficioItem2) return -1;
		        if (custoBeneficioItem1 < custoBeneficioItem2) return 1;
		        return 0;
		    } 
		};
	}
	
	
	public boolean equals(Item item) {
		if(this.label != item.label) {
			return false;
		}
		if(this.valor != item.valor) {
			return false;
		}
		if(this.d1 != item.d1) {
			return false;
		}
		if(this.d2 != item.d2) {
			return false;
		}
		if(this.d3 != item.d3) {
			return false;
		}
		if(this.x != item.x) {
			return false;
		}
		return true;
	}
	
	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public double getD1() {
		return d1;
	}

	public void setD1(double d1) {
		this.d1 = d1;
	}

	public double getD2() {
		return d2;
	}

	public void setD2(double d2) {
		this.d2 = d2;
	}

	public double getD3() {
		return d3;
	}

	public void setD3(double d3) {
		this.d3 = d3;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}
}
