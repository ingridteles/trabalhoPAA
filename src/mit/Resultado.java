package mit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Resultado {

	private Double valorFuncaoObjetivo;
	private List<Item> variaveisInteiras;
	private List<Item> variaveisFracionadas;
	private int passos;

	public Resultado() {
		this.valorFuncaoObjetivo = MochilaInteiraTridimensional.SOLUCAO_INVIAVEL;
		this.variaveisInteiras = new ArrayList<>();
		this.variaveisFracionadas = new ArrayList<>();
	}

	public Double getValorFuncaoObjetivo() {
		return valorFuncaoObjetivo;
	}

	public void setValorFuncaoObjetivo(Double valorFuncaoObjetivo) {
		this.valorFuncaoObjetivo = valorFuncaoObjetivo;
	}

	public List<Item> getVariaveisInteiras() {
		return variaveisInteiras;
	}

	public void setVariaveisInteiras(List<Item> variaveisInteiras) {
		this.variaveisInteiras = variaveisInteiras;
	}

	public List<Item> getVariaveisFracionadas() {
		return variaveisFracionadas;
	}

	public void setVariaveisFracionadas(List<Item> variaveisFracionadas) {
		this.variaveisFracionadas = variaveisFracionadas;
	}

	public int getPassos() {
		return passos;
	}

	public void setPassos(int passos) {
		this.passos = passos;
	}

	public boolean equals(Resultado resultado) {

		if (!this.valorFuncaoObjetivo.equals(resultado.getValorFuncaoObjetivo())) {
			return false;
		}

		List<Integer> listaFrac = new ArrayList<>();
		listaFrac = this.variaveisFracionadas.stream().map(i -> i.getIndice()).collect(Collectors.toList());
		List<Integer> listaFracRes = new ArrayList<>();
		listaFracRes = resultado.getVariaveisFracionadas().stream().map(i -> i.getIndice()).collect(Collectors.toList());
		listaFrac.removeAll(listaFracRes);
		boolean listasDiferentes = (listaFrac.size() != 0);

		if (listasDiferentes) {
			return false;
		}

		List<Integer> listaInt = new ArrayList<>();
		listaInt = this.variaveisFracionadas.stream().map(i -> i.getIndice()).collect(Collectors.toList());
		List<Integer> listaIntResultado = new ArrayList<>();
		listaIntResultado = resultado.getVariaveisFracionadas().stream().map(i -> i.getIndice()).collect(Collectors.toList());
		listaInt.removeAll(listaIntResultado);
		listasDiferentes = (listaInt.size() != 0);

		if (listasDiferentes) {
			return false;
		}

		return true;
	}

	public void imprimir(String ladoRamificacao) {
		System.out.println("\n\n******************* SOLUÇÃO FINAL LADO " + ladoRamificacao +" ********************************");
		if (!this.valorFuncaoObjetivo.equals(-1D)) {
			StringBuilder builder = new StringBuilder();
			builder.append("\nValor da melhor solução: ");
			builder.append(this.valorFuncaoObjetivo);
			builder.append("\n");
			builder.append("Multi-conjunto: ");

			Collections.sort(this.variaveisInteiras, Item.porIndice());

			List<Item> variaveisInteirasNaoNulas = this.variaveisInteiras.stream().filter(i -> i.getX() != 0)
					.collect(Collectors.toList());

			String strItens = variaveisInteirasNaoNulas.stream()
					.map(item -> "\nLabel: m(S, " + item.getLabel() + ") = " + item.getX() + "")
					.collect(Collectors.joining(", "));
			
			String strItensIndice = variaveisInteirasNaoNulas.stream()
					.map(item -> "\nIndice: m(S, " + item.getIndice() + ") = " + item.getX() + "")
					.collect(Collectors.joining(", "));

			//builder.append(strItens);
			builder.append(strItensIndice);

			System.out.println(builder.toString());
			System.out.println("Número de passos: " + this.passos);
		} else {
			System.out.println("\nSolução não encontrada.");
		}
	}
}
