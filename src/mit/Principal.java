package mit;

import java.time.LocalDateTime;

public class Principal {

	public static final String CONSIDERAR_MAIOR_SOLUCAO = "MAIOR";
	public static final String CONSIDERAR_MENOR_SOLUCAO = "MENOR";

	public static void main(String[] args) {

		LocalDateTime dataHoraProcessamento = LocalDateTime.now();
		String duracao = "";

		String path = "instancias_knapsack_3d/";
		// String intancia = "teste2.3kp";
		// String intancia = "teste-2.3kp";
		// String intancia = "teste-3.3kp";
		// String intancia = "teste-5.3kp";
		// String intancia = "ep3-20-C-C-50.3kp";
		// String intancia = "ep3-20-C-C-90.3kp";
		// String intancia = "ep3-20-C-R-50.3kp";
		// String intancia = "facil0";
		  String intancia = "facil1"; // ok
		// String intancia = "facil2"; // ok
		// String intancia = "facil3"; // ok
		// String intancia = "facil4"; // ok
		// String intancia = "media1"; // ok
		// String intancia = "media2";
		// String intancia = "media3"; // ok
		// String intancia = "dificil1";
		// String intancia = "dificil2";
		// String intancia = "dificil3";
		// System.out.println("\n\n\n******************** " + intancia + "
		// **************************************");

		Entrada.lerEntrada(path + intancia);

		long tempoEsperaSegundos = 60; // 1min
		// long tempoEsperaSegundos = 300; // 5min
		// long tempoEsperaSegundos = 600; // 10min
		// long tempoEsperaSegundos = 900; // 15min
		// long tempoEsperaSegundos = 1800; // meia hora
		// long tempoEsperaSegundos = 3600; // uma hora

		// MochilaInteiraTridimensional mochilaInteiraTridimensional = new
		// MochilaInteiraTridimensional();
        // mochilaInteiraTridimensional.resolver(Entrada.n, Entrada.W1, Entrada.W2, Entrada.W3, Entrada.itens);

		MochilaInteiraTridimensionalLado mitComLado = new MochilaInteiraTridimensionalLado();
		// System.out.println("********** escolhendo primeiro o lado o problema que
		// retorna o menor valor de função objetivo ********");
		Resultado resultadoDireita = mitComLado.resolver(Entrada.n, Entrada.W1, Entrada.W2, Entrada.W3, Entrada.itens,
				CONSIDERAR_MENOR_SOLUCAO, tempoEsperaSegundos);
		resultadoDireita.imprimir(CONSIDERAR_MENOR_SOLUCAO);
		// System.out.println("\n\n\n******************** fim
		// **************************************");

		// System.out.println("********* escolhendo primeiro o lado o problema que
		// retorna o maior valor de função objetivo *************");
		Resultado resultadoEsquerda = mitComLado.resolver(Entrada.n, Entrada.W1, Entrada.W2, Entrada.W3, Entrada.itens,
				CONSIDERAR_MAIOR_SOLUCAO, tempoEsperaSegundos);
		resultadoEsquerda.imprimir(CONSIDERAR_MAIOR_SOLUCAO);
		// System.out.println("\n\n\n******************** fim lado esquedo
		// **************************************");

		/*
		 * if (resultadoDireita.getValorFuncaoObjetivo() >
		 * resultadoEsquerda.getValorFuncaoObjetivo()) {
		 * System.out.println("\nLado Direito"); resultadoDireita.imprimir(); } else {
		 * System.out.println("\nLado Esquerdo"); resultadoEsquerda.imprimir(); }
		 */
		LocalDateTime dataHoraProcessamentoFim = LocalDateTime.now();

		long diferencaEmHoras = java.time.temporal.ChronoUnit.HOURS.between(dataHoraProcessamento,
				dataHoraProcessamentoFim);
		long diferencaEmMinutos = java.time.temporal.ChronoUnit.MINUTES.between(dataHoraProcessamento,
				dataHoraProcessamentoFim);
		long diferencaEmSegundos = java.time.temporal.ChronoUnit.SECONDS.between(dataHoraProcessamento,
				dataHoraProcessamentoFim);
		long diferencaEmMiliSegundos = java.time.temporal.ChronoUnit.MILLIS.between(dataHoraProcessamento,
				dataHoraProcessamentoFim);

		duracao = diferencaEmHoras + "h  " + diferencaEmMinutos + "min  " + diferencaEmSegundos + "s  "
				+ diferencaEmMiliSegundos + "mili";

		System.out.println("\nTempo de execução " + intancia + ": " + duracao);
	}
}
