package mit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MochilaInteiraTridimensional {

	public static final double SOLUCAO_INVIAVEL = -1D;

	public void resolver(int n, Double W1, Double W2, Double W3, List<Item> itens) {

		ProblemaMIT relaxacaoDoProblema = new ProblemaMIT();
		relaxacaoDoProblema.calcularSolucaoViaCplex(n, W1, W2, W3, itens, relaxacaoDoProblema.getId());

		ProblemaMIT heuristicaDoProblema = new ProblemaMIT();
		heuristicaDoProblema.calcularSolucaoInicialComHeuristicaGulosa(n, W1, W2, W3, itens, relaxacaoDoProblema);

		if (relaxacaoDoProblema.getResultado().getValorFuncaoObjetivo() == SOLUCAO_INVIAVEL) {
			System.out.println("A relaxação do modelo não possui solução viável.");
			System.exit(0);
		} else {

			Resultado resultadoFinal = new Resultado();
			List<Resultado> resultadosEncontrados = new ArrayList<>();

			resultadosEncontrados.add(relaxacaoDoProblema.getResultado());

			List<ProblemaMIT> listaDeProblemas = new ArrayList<>();
			listaDeProblemas.add(relaxacaoDoProblema);

			if (heuristicaDoProblema.getResultado().getValorFuncaoObjetivo() < relaxacaoDoProblema.getResultado()
					.getValorFuncaoObjetivo()) {
				resultadosEncontrados.add(heuristicaDoProblema.getResultado());
				resultadoFinal.setValorFuncaoObjetivo(heuristicaDoProblema.getResultado().getValorFuncaoObjetivo());
				resultadoFinal.setVariaveisInteiras(heuristicaDoProblema.getResultado().getVariaveisInteiras());
			}

			while (listaDeProblemas.size() != 0) {
				ProblemaMIT subproblema = recuperarProblema(listaDeProblemas);
				listaDeProblemas.remove(0);
				// esperarSegundos(4);
				if (subproblema.getResultado().getVariaveisFracionadas().size() == 0) {
					// Solução inteira encontrada.
					if (subproblema.getResultado().getValorFuncaoObjetivo() > resultadoFinal.getValorFuncaoObjetivo()) {

						resultadoFinal.setValorFuncaoObjetivo(subproblema.getResultado().getValorFuncaoObjetivo());
						resultadoFinal.setVariaveisInteiras(subproblema.getResultado().getVariaveisInteiras());

						imprimirResultadoParcial(subproblema);
					}
				} else {
					executarBranchAndBound(subproblema, n, W1, W2, W3, itens, listaDeProblemas, resultadosEncontrados);
				}
			}
			resultadoFinal.imprimir("GERAL");
		}
	}
	
	private ProblemaMIT recuperarProblema(List<ProblemaMIT> listaDeProblemas) {

		Collections.sort(listaDeProblemas, ProblemaMIT.porValorFuncaoObjetivo());
		//System.out.println(listaDeProblemas.stream().map(p -> String.valueOf(p.getResultado().getValorFuncaoObjetivo()))
			//	.collect(Collectors.joining(", ", "\nProblema Por Valor Funcao Objetivo: [", "]")));
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
			List<Item> itens, List<ProblemaMIT> listaDeProblemas, List<Resultado> resultadosEncontrados) {

		// for (Item variavelEscolhida :
		// subproblema.getResultado().getVariaveisFracionadas()) {
		// for (int i = 0; i <
		// subproblema.getResultado().getVariaveisFracionadas().size(); i++) {

		List<Item> listaDeVariaveisFracionadas = new ArrayList<>();
		// TODO ordenar e diminuir aqui
		listaDeVariaveisFracionadas.addAll(subproblema.getResultado().getVariaveisFracionadas());

		while (listaDeVariaveisFracionadas.size() != 0) {

			Item variavelEscolhida = retornarVariavelARamificar(listaDeVariaveisFracionadas);
			listaDeVariaveisFracionadas.remove(0);

			ProblemaMIT subProblemaComRestricaoMenorIgual = resolverSubProblema(subproblema, variavelEscolhida, "<=", n,
					W1, W2, W3, itens);

			ProblemaMIT subProblemaComRestricaoMaiorIgual = resolverSubProblema(subproblema, variavelEscolhida, ">=", n,
					W1, W2, W3, itens);

			if (verificarPermissaoEmpilhar(subProblemaComRestricaoMenorIgual, subProblemaComRestricaoMaiorIgual,
					resultadosEncontrados)) {
				listaDeProblemas.add(subProblemaComRestricaoMenorIgual);
				resultadosEncontrados.add(subProblemaComRestricaoMenorIgual.getResultado());
			}

			if (verificarPermissaoEmpilhar(subProblemaComRestricaoMaiorIgual, subProblemaComRestricaoMenorIgual,
					resultadosEncontrados)) {
				listaDeProblemas.add(subProblemaComRestricaoMaiorIgual);
				resultadosEncontrados.add(subProblemaComRestricaoMaiorIgual.getResultado());
			}
		}
	}

	private ProblemaMIT resolverSubProblema(ProblemaMIT subproblema, Item variavelEscolhida, String simboloRestricao,
			int n, Double W1, Double W2, Double W3, List<Item> itens) {

		Restricao novaRestricao = new Restricao(variavelEscolhida.getIndice(), simboloRestricao,
				variavelEscolhida.getX());

		List<Restricao> restricoesAdicionais = ajustarRestricoesOpostas(subproblema.getRestricoes(), novaRestricao);

		ProblemaMIT subProblemaComRestricaoAdicionais = null;

		if (!encontrarIncompatibilidadeNasRestricoes(restricoesAdicionais)) {
			subProblemaComRestricaoAdicionais = new ProblemaMIT(subproblema, restricoesAdicionais);
			subProblemaComRestricaoAdicionais.calcularSolucaoViaCplex(n, W1, W2, W3, itens,
					subProblemaComRestricaoAdicionais.getId());
		}

		return subProblemaComRestricaoAdicionais;
	}

	private List<Restricao> ajustarRestricoesOpostas(List<Restricao> restricoesExistentes, Restricao novaRestricao) {
		List<Restricao> restricoesAdicionais = new ArrayList<>();

		restricoesAdicionais.addAll(restricoesExistentes);
		restricoesAdicionais.add(novaRestricao);

		for (Restricao r : restricoesExistentes) {
			if (r.ehRestricaoOposta(novaRestricao)) {
				// troca duas restrições opostas por uma
				restricoesAdicionais.remove(r);
				restricoesAdicionais.remove(novaRestricao);
				restricoesAdicionais.add(new Restricao(novaRestricao.getIndice(), "==", novaRestricao.getXinteiro()));
			}
		}
		return restricoesAdicionais;
	}

// TODO  verificar se estou misturando as variaves de um lado para o outro do ramo
	private boolean encontrarIncompatibilidadeNasRestricoes(List<Restricao> restricoesAdicionais) {

		List<Restricao> restricoesMaiorIgual = restricoesAdicionais.stream()
				.filter(r -> r.getSimboloRestricao().equals(">=")).collect(Collectors.toList());
		Collections.sort(restricoesMaiorIgual, Restricao.porIndice());

		List<Restricao> restricoesMenorIgual = restricoesAdicionais.stream()
				.filter(r -> r.getSimboloRestricao().equals("<=")).collect(Collectors.toList());
		Collections.sort(restricoesMenorIgual, Restricao.porIndice());

		int menorTamanho = restricoesMaiorIgual.size() < restricoesMenorIgual.size() ? restricoesMaiorIgual.size()
				: restricoesMenorIgual.size();

		for (int i = 0; i < menorTamanho; i++) {
			if (restricoesMenorIgual.get(0).getXinteiro() < restricoesMaiorIgual.get(0).getXinteiro()) {
				return true;
			}
		}
		return false;
	}

	private boolean verificarPermissaoEmpilhar(ProblemaMIT subProbelma1, ProblemaMIT subProbelma2,
			List<Resultado> resultadosEncontrados) {

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
		/*
		 * if (subProbelma2 != null) { if
		 * (subProbelma1.getResultado().getValorFuncaoObjetivo() <
		 * subProbelma2.getResultado() .getValorFuncaoObjetivo()) { return false; } }
		 */

		if (subProbelma2 != null) {
			if (subProbelma1.getResultado().getValorFuncaoObjetivo() > subProbelma2.getResultado()
					.getValorFuncaoObjetivo()) {
				return false;
			}
		}
		return true;
	}

	private void esperarSegundos(int i) {
		try {
			Thread.currentThread();
			Thread.sleep(i * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
}
