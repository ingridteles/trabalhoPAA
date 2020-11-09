package mit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MochilaInteiraTridimensionalLado {

	public static final double SOLUCAO_INVIAVEL = -1D;
	public static final String CONSIDERAR_MAIOR_SOLUCAO = "MAIOR";
	public static final String CONSIDERAR_MENOR_SOLUCAO = "MENOR";

	public Resultado resolver(int n, Double W1, Double W2, Double W3, List<Item> itens, String ladoRamificacao,
			long tempoEsperaSegundos) {

		Resultado resultadoFinal = new Resultado();

		LocalDateTime dataHoraProcessamento = LocalDateTime.now();
		String duracao = "";
		boolean podeParar = false;

		ProblemaMIT relaxacaoDoProblema = new ProblemaMIT();
		relaxacaoDoProblema.calcularSolucaoViaCplex(n, W1, W2, W3, itens, relaxacaoDoProblema.getId());

		ProblemaMIT heuristicaDoProblema = new ProblemaMIT();
		heuristicaDoProblema.calcularSolucaoInicialComHeuristicaDeArredondamento(relaxacaoDoProblema);

		if (relaxacaoDoProblema.getResultado().getValorFuncaoObjetivo() == SOLUCAO_INVIAVEL) {
			System.out.println("A relaxação do modelo não possui solução viável.");
			System.exit(0);
		} else {
			List<Resultado> resultadosEncontrados = new ArrayList<>();

			resultadosEncontrados.add(relaxacaoDoProblema.getResultado());
			resultadosEncontrados.add(heuristicaDoProblema.getResultado());
			resultadoFinal.setValorFuncaoObjetivo(heuristicaDoProblema.getResultado().getValorFuncaoObjetivo());
			resultadoFinal.setVariaveisInteiras(heuristicaDoProblema.getResultado().getVariaveisInteiras());
			resultadoFinal.setPassos(relaxacaoDoProblema.getId());

			List<ProblemaMIT> listaDeProblemas = new ArrayList<>();
			listaDeProblemas.add(relaxacaoDoProblema);

			while (listaDeProblemas.size() != 0 && podeParar == false) {
				ProblemaMIT subproblema = recuperarProblema(listaDeProblemas);
				listaDeProblemas.remove(0);
				// esperarSegundos(4);
				if (subproblema.getResultado().getVariaveisFracionadas().size() == 0) {
					// Solução inteira encontrada.
					if (subproblema.getResultado().getValorFuncaoObjetivo() > resultadoFinal.getValorFuncaoObjetivo()) {

						resultadoFinal.setValorFuncaoObjetivo(subproblema.getResultado().getValorFuncaoObjetivo());
						resultadoFinal.setVariaveisInteiras(subproblema.getResultado().getVariaveisInteiras());
						resultadoFinal.setPassos(subproblema.getId());

						// imprimirResultadoParcial(subproblema);
					}
				} else {
					executarBranchAndBound(subproblema, n, W1, W2, W3, itens, listaDeProblemas, resultadosEncontrados,
							ladoRamificacao);
				}

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

				if (diferencaEmSegundos >= tempoEsperaSegundos) {
					podeParar = true;
					System.out.println("diferencaEmMinutos: " + diferencaEmMinutos);
					System.out.println("diferencaEmSegundos: " + diferencaEmSegundos);
					System.out.println("listaDeProblemas.size(): " + listaDeProblemas.size());
				}
			}
		}
		// System.out.println("Duracao: " + duracao);
		return resultadoFinal;
	}

	private ProblemaMIT recuperarProblema(List<ProblemaMIT> listaDeProblemas) {

		Collections.sort(listaDeProblemas, ProblemaMIT.porValorFuncaoObjetivo());
		// System.out.println(listaDeProblemas.stream().map(p ->
		// String.valueOf(p.getResultado().getValorFuncaoObjetivo()))
		// .collect(Collectors.joining(", ", "\nProblema Por Valor Funcao Objetivo: [",
		// "]")));
		return listaDeProblemas.get(0);
	}

	private void imprimirResultadoParcial(ProblemaMIT subproblema) {
		System.out.println("\nSolução inteira encontrada!!\n");

		System.out.printf("\nNivel: %s \n   Valor da Função Objetivo: %s\n", subproblema.getId(),
				subproblema.getResultado().getValorFuncaoObjetivo());
		System.out.printf("\n   Valor das variáveis inteiras: ");
		subproblema.getResultado().getVariaveisInteiras().stream().filter(i -> i.getX() > 0)
				.forEach(item -> System.out.printf("x_%s = %s  ", item.getLabel(), item.getX()));
	}

	public void executarBranchAndBound(ProblemaMIT subproblema, int n, Double W1, Double W2, Double W3,
			List<Item> itens, List<ProblemaMIT> listaDeProblemas, List<Resultado> resultadosEncontrados,
			String ladoRamificacao) {

		// for (Item variavelEscolhida :
		// subproblema.getResultado().getVariaveisFracionadas()) {
		// for (int i = 0; i <
		// subproblema.getResultado().getVariaveisFracionadas().size(); i++) {

		List<Item> listaDeVariaveisFracionadas = new ArrayList<>();
		// TODO ordenar e diminuir aqui
		listaDeVariaveisFracionadas.addAll(subproblema.getResultado().getVariaveisFracionadas());
		// int metadeDasVariaveis = (listaDeVariaveisFracionadas.size() / 2) + 1;
		while (listaDeVariaveisFracionadas.size() != 0) {
			// while (metadeDasVariaveis > 0) {
			// metadeDasVariaveis = metadeDasVariaveis - 1;
			Item variavelEscolhida = retornarVariavelARamificar(listaDeVariaveisFracionadas);
			listaDeVariaveisFracionadas.remove(0);

			ProblemaMIT subProblemaComRestricaoMenorIgual = resolverSubProblema(subproblema, variavelEscolhida, "<=", n,
					W1, W2, W3, itens);

			ProblemaMIT subProblemaComRestricaoMaiorIgual = resolverSubProblema(subproblema, variavelEscolhida, ">=", n,
					W1, W2, W3, itens);

			if (verificarPermissaoEmpilhar(subProblemaComRestricaoMenorIgual, subProblemaComRestricaoMaiorIgual,
					resultadosEncontrados, ladoRamificacao)) {
				listaDeProblemas.add(subProblemaComRestricaoMenorIgual);
				resultadosEncontrados.add(subProblemaComRestricaoMenorIgual.getResultado());
			}
			if (verificarPermissaoEmpilhar(subProblemaComRestricaoMaiorIgual, subProblemaComRestricaoMenorIgual,
					resultadosEncontrados, ladoRamificacao)) {
				listaDeProblemas.add(subProblemaComRestricaoMaiorIgual);
				resultadosEncontrados.add(subProblemaComRestricaoMaiorIgual.getResultado());
			}
		}
	}

	private ProblemaMIT resolverSubProblema(ProblemaMIT subproblema, Item variavelEscolhida, String simboloRestricao,
			int n, Double W1, Double W2, Double W3, List<Item> itens) {

		Restricao novaRestricao = new Restricao(variavelEscolhida.getIndice(), simboloRestricao,
				variavelEscolhida.getX());

		List<Restricao> restricoesAdicionais = adicionarRestricao(subproblema.getRestricoes(), novaRestricao);

		ProblemaMIT subProblemaComRestricaoAdicionais = null;

		if (restricoesAdicionais.size() != 0) {
			subProblemaComRestricaoAdicionais = new ProblemaMIT(subproblema, restricoesAdicionais);
			subProblemaComRestricaoAdicionais.calcularSolucaoViaCplex(n, W1, W2, W3, itens,
					subProblemaComRestricaoAdicionais.getId());
		}

		return subProblemaComRestricaoAdicionais;
	}

	private List<Restricao> adicionarRestricao(List<Restricao> restricoesExistentes, Restricao novaRestricao) {

		List<Restricao> todasRestricoes = new ArrayList<>();
		todasRestricoes.addAll(restricoesExistentes);
		todasRestricoes.add(novaRestricao);

		ajustarRestricoes(restricoesExistentes, novaRestricao, todasRestricoes);

		return todasRestricoes;
	}

	private void ajustarRestricoes(List<Restricao> restricoesExistentes, Restricao novaRestricao,
			List<Restricao> todasRestricoes) {

		// caso em que a mesma variável é, por exemplo, x_1 >= 5 e x_1 <= 3 então
		// retorna uma lista vazia para indicar que encontrou incompatibilidade nas
		// restricoes e não precisa resolver esse novo problema
		if (encontrarIncompatibilidadeNasRestricoes(todasRestricoes)) {
			todasRestricoes = new ArrayList<>();
		}

		for (Restricao r : restricoesExistentes) {
			// caso em que já foi resolvido um problema com restrição mais
			// restritiva para esta variável a ramificar retorna uma lista vazia para
			// indicar que não precisa resolver esse novo problema
			if (r.ehMaisRestritiva(novaRestricao)) {
				todasRestricoes = new ArrayList<>();
			}
			// caso em que a mesma variável é >= e <= ao memos valor.
			// Ex.: x_1 >= 1 e x_1 <= 1 então troca por x_1 = 1
			if (r.ehRestricaoOposta(novaRestricao)) {
				todasRestricoes.remove(r);
				todasRestricoes.remove(novaRestricao);
				todasRestricoes.add(new Restricao(novaRestricao.getIndice(), "==", novaRestricao.getXinteiro()));
			}
		}
	}

	private boolean encontrarIncompatibilidadeNasRestricoes(List<Restricao> restricoes) {

		List<Restricao> restricoesMaiorIgual = restricoes.stream().filter(r -> r.getSimboloRestricao().equals(">="))
				.collect(Collectors.toList());
		Collections.sort(restricoesMaiorIgual, Restricao.porIndice());

		List<Restricao> restricoesMenorIgual = restricoes.stream().filter(r -> r.getSimboloRestricao().equals("<="))
				.collect(Collectors.toList());
		Collections.sort(restricoesMenorIgual, Restricao.porIndice());

		int menorTamanho = restricoesMaiorIgual.size() < restricoesMenorIgual.size() ? restricoesMaiorIgual.size()
				: restricoesMenorIgual.size();

		for (int i = 0; i < menorTamanho; i++) {
			if (restricoesMenorIgual.get(0).getXinteiro() > restricoesMaiorIgual.get(0).getXinteiro()) {
				return true;
			}
		}
		return false;
	}

	private boolean verificarPermissaoEmpilhar(ProblemaMIT subProbelma1, ProblemaMIT subProbelma2,
			List<Resultado> resultadosEncontrados, String ladoRamificacao) {

		// subproblema não resolvido por incompatibilidade encontrada nas restrições.
		// Ex.: x_0 >= 2 e x_0 <= 0.
		if (subProbelma1 == null) {
			return false;
		}

		// Solução inviável não precisa entrar na pilha
		if (subProbelma1.getResultado().getValorFuncaoObjetivo() == SOLUCAO_INVIAVEL) {
			return false;
		}

		// Solução já encontrada não precisa entrar na pilha
		for (Resultado r : resultadosEncontrados) {
			if (r.equals(subProbelma1.getResultado())) {
				return false;
			}
		}

		if (ladoRamificacao.equals(CONSIDERAR_MAIOR_SOLUCAO)) {
			if (subProbelma2 != null) {
				if (subProbelma1.getResultado().getValorFuncaoObjetivo() < subProbelma2.getResultado()
						.getValorFuncaoObjetivo()) {
					return false;
				}
			}
		}

		if (ladoRamificacao.equals(CONSIDERAR_MENOR_SOLUCAO)) {
			if (subProbelma2 != null) {
				if (subProbelma1.getResultado().getValorFuncaoObjetivo() > subProbelma2.getResultado()
						.getValorFuncaoObjetivo()) {
					return false;
				}
			}
		}
		return true;
	}

	private Item retornarVariavelARamificar(List<Item> variaveisFracionadas) {
		Collections.sort(variaveisFracionadas, Item.porValorDaVariavelX());
		/*
		 * System.out.println(variaveisFracionadas.stream().map(item ->
		 * String.valueOf(item.getX())) .collect(Collectors.joining(", ",
		 * "\nItem Por Valor Da Variavel: [", "]")));
		 */
		return variaveisFracionadas.get(0);
	}

	private void esperarSegundos(int i) {
		try {
			Thread.currentThread();
			Thread.sleep(i * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
