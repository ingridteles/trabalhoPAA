package inteiro;

import java.util.ArrayList;
import java.util.List;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import mit.Entrada;
import mit.Item;

public class MitInteiro {

	public static void main(String[] args) {
		String path = "instancias_knapsack_3d/";
		String intancia = "teste2.3kp";
		// String intancia = "teste-2.3kp";
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

		Entrada.lerEntrada(path + intancia);

		calcularSolucaoViaCplex(Entrada.n, Entrada.W1, Entrada.W2, Entrada.W3, Entrada.itens);
	}

	/*
	 * Calcula o valor da solução da instância chamando o solver com as variáveis inteiras
	 * para saber o valor da solução ótima e comparar com o valor encontrado pelo algoritmo branch and bond.
	 */
	public static void calcularSolucaoViaCplex(int n, Double W1, Double W2, Double W3, List<Item> itens) {

		try {
			IloCplex cplex = new IloCplex();

			IloNumVar[] x = cplex.numVarArray(n, 0, Integer.MAX_VALUE, IloNumVarType.Int);
			// IloNumVar[] x = cplex.numVarArray(n, 0.0, Double.MAX_VALUE,
			// IloNumVarType.Float);

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

			cplex.setOut(null);

			if (cplex.solve()) {
				System.out.printf("\nValor da Função Objetivo: %s\nValor das variáveis não nulas: ", cplex.getObjValue());

				for (int i = 0; i < n; i++) {
					double s = Math.floor(cplex.getValue(x[i]) * 100) / 100;
					if (s != 0) {
						System.out.printf("x_%s = %s  ", i, s);
					}
				}
			} else {
				System.out.println("\n Solução inviável.");
			}
			System.out.println("\n");
			cplex.exportModel("ModeloInteiro.lp");
			cplex.end();
			cplex.close();
		} catch (IloException exc) {
			exc.printStackTrace();
		}
	}
}
