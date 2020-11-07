package mit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

public class ProblemaMIT {

	private int id;
	private Resultado resultado;
	private List<Restricao> restricoes;

	public ProblemaMIT() {
		this.id = 0;
		this.resultado = new Resultado();
		this.restricoes = new ArrayList<>();
	}

	public ProblemaMIT(ProblemaMIT pai, List<Restricao> restricoesAdicionais) {
		this.id = pai.id + 1;
		this.resultado = new Resultado();
		this.restricoes = new ArrayList<>();
		this.restricoes.addAll(restricoesAdicionais);
	}

	public void calcularSolucaoInicialComHeuristicaGulosa(int n, Double W1, Double W2, Double W3, List<Item> itens,
			ProblemaMIT relaxacaoDoProblema) {
		System.out.println("\n\nInicio calcularSolucaoInicialComHeuristicaGulosa");
		// Ordena os itens por custo benefício decrescente
		List<Item> itensOrdenados = new ArrayList<>();
		itensOrdenados.addAll(itens);
		Collections.sort(itensOrdenados, Item.porCustoBeneficio());
		Double capacidadeTotal = W1 * W2 * W3;
		Double peso = 0.0;
		Double valorFuncaoObjetivo = 0.0;
		List<Item> variaveisInteiras = new ArrayList<>();
		List<Item> variaveisFracionadas = new ArrayList<>();
		boolean sobrouCapacidade = true;

		Item maiorVariavelProblemaRelaxado = retornarMaiorVariavelProblemaRelaxado(relaxacaoDoProblema);

		variaveisInteiras.add(maiorVariavelProblemaRelaxado);
		peso = maiorVariavelProblemaRelaxado.getD1() * maiorVariavelProblemaRelaxado.getD2()
				* maiorVariavelProblemaRelaxado.getD3();
		valorFuncaoObjetivo = maiorVariavelProblemaRelaxado.getValor() * maiorVariavelProblemaRelaxado.getX();

		int indice = 0;
		for (int i = 0; i < itensOrdenados.size(); i++) {
			if (itensOrdenados.get(i).getLabel() == maiorVariavelProblemaRelaxado.getLabel()) {
				indice = i;
			}
		}
		itensOrdenados.remove(indice);

		Item itemSelecionado = null;
		while (peso < capacidadeTotal) {
			if (itensOrdenados.size() == 0) {
				sobrouCapacidade = false;
				break;
			}
			itemSelecionado = itensOrdenados.get(0);
			itensOrdenados.remove(0);
			itemSelecionado.setX(1);
			variaveisInteiras.add(itemSelecionado);
			peso = peso + (itemSelecionado.getD1() * itemSelecionado.getD2() * itemSelecionado.getD3());
			valorFuncaoObjetivo = valorFuncaoObjetivo + itemSelecionado.getValor();
		}

		if (sobrouCapacidade) {
			Double espacoRestante = capacidadeTotal - peso;
			if (espacoRestante > 0) {
				itemSelecionado = itensOrdenados.get(0);
				itemSelecionado.setX(
						espacoRestante / (itemSelecionado.getD1() * itemSelecionado.getD2() * itemSelecionado.getD3()));
				variaveisFracionadas.add(itemSelecionado);
				valorFuncaoObjetivo = valorFuncaoObjetivo + itemSelecionado.getValor() * itemSelecionado.getX();
			}
		}

		if (valorFuncaoObjetivo > relaxacaoDoProblema.getResultado().getValorFuncaoObjetivo()) {
			int indiceUltimaVariavel = variaveisInteiras.size() - 1;
			valorFuncaoObjetivo = valorFuncaoObjetivo - (variaveisInteiras.get(indiceUltimaVariavel).getValor()
					* variaveisInteiras.get(indiceUltimaVariavel).getX());
			variaveisInteiras.remove(indiceUltimaVariavel);
		}

		this.resultado.setValorFuncaoObjetivo(valorFuncaoObjetivo);
		this.resultado.setVariaveisInteiras(variaveisInteiras);
		this.resultado.setVariaveisFracionadas(variaveisFracionadas);
/*
		System.out.println("Funcao objetivo: " + valorFuncaoObjetivo);
		System.out.println(variaveisInteiras.stream().map(i -> "x_" + i.getLabel() + " = " + i.getX())
				.collect(Collectors.joining(", ", "variaveis inteiras: [", "]")));
		System.out.println(variaveisFracionadas.stream().map(i -> "x_" + i.getLabel() + " = " + i.getX())
				.collect(Collectors.joining(", ", "variavel fracionada: [", "]")));
		System.out.println("Fim calcularSolucaoInicialComHeuristicaGulosa\n\n");
		*/
	}
	
	
	public void calcularSolucaoInicialComHeuristicaDeArredondamento(ProblemaMIT relaxacaoDoProblema) {
		System.out.println("\n\nInicio Calcular Solucao Inicial Com Heuristica de Arredondamento");

		Double valorFuncaoObjetivo = relaxacaoDoProblema.getResultado().getValorFuncaoObjetivo();
		List<Item> variaveisInteiras = new ArrayList<>();
		variaveisInteiras.addAll(relaxacaoDoProblema.getResultado().getVariaveisInteiras());
		
		for(Item item: relaxacaoDoProblema.getResultado().getVariaveisFracionadas()) {
			valorFuncaoObjetivo = valorFuncaoObjetivo - (item.getX()*item.getValor());  
			item.setX((int) item.getX());
			variaveisInteiras.add(item);
			valorFuncaoObjetivo = valorFuncaoObjetivo + (item.getX()*item.getValor());
		}
	
		this.resultado.setValorFuncaoObjetivo(valorFuncaoObjetivo);
		this.resultado.setVariaveisInteiras(variaveisInteiras);
		this.resultado.setVariaveisFracionadas(new ArrayList<>());
	}

	private Item retornarMaiorVariavelProblemaRelaxado(ProblemaMIT relaxacaoDoProblema) {

		Collections.sort(relaxacaoDoProblema.getResultado().getVariaveisFracionadas(), Item.porValorDaVariavelX());

		Collections.sort(relaxacaoDoProblema.getResultado().getVariaveisInteiras(), Item.porValorDaVariavelX());

		if ((int) relaxacaoDoProblema.getResultado().getVariaveisFracionadas().get(0).getX() > relaxacaoDoProblema
				.getResultado().getVariaveisInteiras().get(0).getX()) {
//			int x = (int) relaxacaoDoProblema.getResultado().getVariaveisFracionadas().get(0).getX();
			relaxacaoDoProblema.getResultado().getVariaveisFracionadas().get(0)
					.setX((int) relaxacaoDoProblema.getResultado().getVariaveisFracionadas().get(0).getX());
			return relaxacaoDoProblema.getResultado().getVariaveisFracionadas().get(0);
		} else {
			return relaxacaoDoProblema.getResultado().getVariaveisInteiras().get(0);
		}
	}

	public void calcularSolucaoViaCplex(int n, Double W1, Double W2, Double W3, List<Item> itens, int id) {

		String nomeModelo = "ModeloProblemaOriginal.lp";
		try {
			IloCplex cplex = new IloCplex();

			IloNumVar[] x = cplex.numVarArray(n, 0.0, Double.MAX_VALUE, IloNumVarType.Float);

			IloLinearNumExpr funcaoObjetivo = cplex.linearNumExpr();
			for (int i = 0; i < n; i++) {
				funcaoObjetivo.addTerm(itens.get(i).getValor(), x[itens.get(i).getLabel()]);
			}

			cplex.addMaximize(funcaoObjetivo);

			IloLinearNumExpr dimensao1 = cplex.linearNumExpr();
			IloLinearNumExpr dimensao2 = cplex.linearNumExpr();
			IloLinearNumExpr dimensao3 = cplex.linearNumExpr();

			List<IloRange> constraints = new ArrayList<IloRange>();
			for (int i = 0; i < n; i++) {
				dimensao1.addTerm(itens.get(i).getD1(), x[itens.get(i).getLabel()]);
				dimensao2.addTerm(itens.get(i).getD2(), x[itens.get(i).getLabel()]);
				dimensao3.addTerm(itens.get(i).getD3(), x[itens.get(i).getLabel()]);

				constraints.add(cplex.addGe(x[itens.get(i).getLabel()], 0));
			}
			constraints.add(cplex.addLe(dimensao1, W1));
			constraints.add(cplex.addLe(dimensao2, W2));
			constraints.add(cplex.addLe(dimensao3, W3));

			IloRange restricao = null;
			for (Restricao r : this.restricoes) {
				if (r.getSimboloRestricao().equals(">=")) {
					restricao = cplex.addGe(x[r.getLabel()], r.getXinteiro());
					imprimir(r, ">=");
					nomeModelo = id + "_Modelo_" + r.getLabel() + "_maiorIgual_" + r.getXinteiro() + ".lp";
				} else if (r.getSimboloRestricao().equals("<=")) {
					restricao = cplex.addLe(x[r.getLabel()], r.getXinteiro());
					imprimir(r, "<=");
					nomeModelo = id + "_Modelo_" + r.getLabel() + "_menorIgual_" + r.getXinteiro() + ".lp";
				} else if (r.getSimboloRestricao().equals("==")) {
					restricao = cplex.addEq(x[r.getLabel()], r.getXinteiro());
					imprimir(r, "==");
					nomeModelo = id + "_Modelo_" + r.getLabel() + "_igual_" + r.getXinteiro() + ".lp";
				}
				constraints.add(restricao);
			}

			cplex.setOut(null);
			// cplex.setOut(logfile);

			if (cplex.solve()) {
				this.resultado.setValorFuncaoObjetivo(cplex.getObjValue());
				//System.out.printf("\nValor da Função Objetivo: %s\nValor das variáveis: ",
				//		this.resultado.getValorFuncaoObjetivo());

				for (int i = 0; i < n; i++) {

					double solucao = arredondar(cplex.getValue(x[i]));

					if (solucao != 0) {
						//System.out.printf("x_%s = %s ", i, solucao);
					}

					if ((int) solucao == solucao) {
						this.resultado.getVariaveisInteiras().add(new Item(itens.get(i), solucao));
					} else {
						this.resultado.getVariaveisFracionadas().add(new Item(itens.get(i), solucao));
					}
				}
			} else {
				this.resultado.setValorFuncaoObjetivo(MochilaInteiraTridimensional.SOLUCAO_INVIAVEL);
				//System.out.println("\n Solução inviável.");
			}
			// System.out.println("\n");
			//cplex.exportModel(nomeModelo);
			cplex.end();
			cplex.close();
		} catch (IloException exc) {
			exc.printStackTrace();
		}
	}

	private double arredondar(double numero) {
		return Math.floor(numero * 100) / 100;
	}

	private void imprimir(Restricao novaRestricao, String simboloRestricao) {
		String CSI = "\u001B[";
		System.out.print(CSI + "32" + "m");
		System.out.printf("\n Restrição adicionada: x_%s %s %s\n", novaRestricao.getLabel(), simboloRestricao,
				novaRestricao.getXinteiro());
		System.out.println(CSI + "m");
	}

	public static Comparator<ProblemaMIT> porValorFuncaoObjetivo() {
		return new Comparator<ProblemaMIT>() {
			public int compare(ProblemaMIT p1, ProblemaMIT p2) {
				if (p1.resultado.getValorFuncaoObjetivo() > p2.resultado.getValorFuncaoObjetivo())
					return -1;
				if (p1.resultado.getValorFuncaoObjetivo() < p2.resultado.getValorFuncaoObjetivo())
					return 1;
				return 0;
			}
		};
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Resultado getResultado() {
		return resultado;
	}

	public void setResultado(Resultado resultado) {
		this.resultado = resultado;
	}

	public List<Restricao> getRestricoes() {
		return restricoes;
	}

	public void setRestricoes(List<Restricao> restricoes) {
		this.restricoes = restricoes;
	}
}