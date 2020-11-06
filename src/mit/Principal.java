package  mit;

public class Principal {

	public static void main(String[] args) {
		String path = "instancias_knapsack_3d/";
		//String intancia = "teste2.3kp";
		String intancia = "teste-2.3kp";
		// String intancia = "teste-3.3kp";
		// String intancia = "teste-5.3kp";
		// String intancia = "ep3-20-C-C-50.3kp";
		// String intancia = "ep3-20-C-C-90.3kp";
		// String intancia = "ep3-20-C-R-50.3kp";
		// String intancia = "facil0";
		// String intancia = "facil1";
		// String intancia = "facil2";
		// String intancia = "facil3";
		// String intancia = "facil4";
		// String intancia = "media1";
		// String intancia = "media2";
		// String intancia = "media3";
		// String intancia = "dificil1";
		// String intancia = "dificil2";
		// String intancia = "dificil3";
		System.out.println("\n\n\n******************** " + intancia + " **************************************");

		Entrada.lerEntrada(path+intancia);

		MochilaInteiraTridimensional mochilaInteiraTridimensional = new MochilaInteiraTridimensional();
		mochilaInteiraTridimensional.resolver(Entrada.n, Entrada.W1, Entrada.W2, Entrada.W3, Entrada.itens);
	
	}
}
