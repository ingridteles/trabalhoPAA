package mit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

public class ProblemaMIT {

	private int identificador;
	private Resultado resultado;
	private List<Restricao> restricoes;

	public int getIdentificador() {
		return identificador;
	}

	public void setIdentificador(int identificador) {
		this.identificador = identificador;
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

	public ProblemaMIT() {
		this.identificador = 0;
		this.resultado = new Resultado();
		this.restricoes = new ArrayList<>();
	}

	public ProblemaMIT(ProblemaMIT pai, List<Restricao> restricoesAdicionais) {
		this.identificador = pai.identificador + 1;
		this.resultado = new Resultado();
		this.restricoes = new ArrayList<>();
		this.restricoes.addAll(restricoesAdicionais);
	}

	public void calcularSolucaoInicialHeuristica() {
		// TODO
	}

	public void calcularSolucaoViaCplex(int n, Double W1, Double W2, Double W3, List<Item> itens) {

		String nomeModelo = "ModeloProblemaOriginal.lp";
		try {
			IloCplex cplex = new IloCplex();

			IloNumVar[] x = cplex.numVarArray(n, 0.0, Double.MAX_VALUE, IloNumVarType.Float);

			// IloNumVar[] x = cplex.numVarArray(n, 0, Integer.MAX_VALUE,
			// IloNumVarType.Int);

			IloLinearNumExpr funcaoObjetivo = cplex.linearNumExpr();
			for (int i = 0; i < n; i++) {
				funcaoObjetivo.addTerm(itens.get(i).getValor(), x[i]);
			}

			cplex.addMaximize(funcaoObjetivo);

			IloLinearNumExpr dimensao1 = cplex.linearNumExpr();
			IloLinearNumExpr dimensao2 = cplex.linearNumExpr();
			IloLinearNumExpr dimensao3 = cplex.linearNumExpr();

			List<IloRange> constraints = new ArrayList<IloRange>();
			for (int i = 0; i < n; i++) {
				dimensao1.addTerm(itens.get(i).getD1(), x[i]);
				dimensao2.addTerm(itens.get(i).getD2(), x[i]);
				dimensao3.addTerm(itens.get(i).getD3(), x[i]);

				constraints.add(cplex.addGe(x[i], 0));
			}
			constraints.add(cplex.addLe(dimensao1, W1));
			constraints.add(cplex.addLe(dimensao2, W2));
			constraints.add(cplex.addLe(dimensao3, W3));

			IloRange restricao = null;
			for (Restricao r : this.restricoes) {
				if (r.getSimboloRestricao().equals(">=")) {
					restricao = cplex.addGe(x[r.getLabel()], r.getXinteiro());
					imprimir(r, ">=");
					nomeModelo = "Modelo_" + r.getLabel() + "_maiorIgual_" + r.getXinteiro() + ".lp";
				} else if (r.getSimboloRestricao().equals("<=")) {
					restricao = cplex.addLe(x[r.getLabel()], r.getXinteiro());
					imprimir(r, "<=");
					nomeModelo = "Modelo_" + r.getLabel() + "_menorIgual_" + r.getXinteiro() + ".lp";
				} else if (r.getSimboloRestricao().equals("==")) {
					restricao = cplex.addEq(x[r.getLabel()], r.getXinteiro());
					imprimir(r, "==");
					nomeModelo = "Modelo_" + r.getLabel() + "_igual_" + r.getXinteiro() + ".lp";
				}
				constraints.add(restricao);
			}

			cplex.setOut(null);
			// cplex.setOut(logfile);

			if (cplex.solve()) {
				this.resultado.setValorFuncaoObjetivo(cplex.getObjValue());
				System.out.printf("\nValor da Função Objetivo: %s\nValor das variáveis: ",
						this.resultado.getValorFuncaoObjetivo());

				for (int i = 0; i < n; i++) {

					double solucao = arredondar(cplex.getValue(x[i]));

					if (solucao != 0) {
						System.out.printf("x_%s = %s ", i, solucao);
					}
					// itens.get(i).x = solucao;
					if ((int) solucao == solucao) {
						this.resultado.getVariaveisInteiras().add(new Item(itens.get(i), solucao));
					} else {
						this.resultado.getVariaveisFracionadas().add(new Item(itens.get(i), solucao));
					}
				}
			} else {
				this.resultado.setValorFuncaoObjetivo(MochilaInteiraTridimensional.SOLUCAO_INVIAVEL);
				System.out.println("\n Solução inviável.");
			}
			// System.out.println("\n");
			cplex.exportModel(nomeModelo);
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
				if (p1.resultado.getValorFuncaoObjetivo() > p2.resultado.getValorFuncaoObjetivo()) return -1;
				if (p1.resultado.getValorFuncaoObjetivo() < p2.resultado.getValorFuncaoObjetivo()) return 1;
				return 0;
			}
		};
	}

}