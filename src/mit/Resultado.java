package mit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Resultado {

	private Double valorFuncaoObjetivo;
	private List<Item> variaveisInteiras;
	private List<Item> variaveisFracionadas;

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

	public boolean equals(Resultado resultado) {

		if (!this.valorFuncaoObjetivo.equals(resultado.getValorFuncaoObjetivo())) {
			return false;
		}

		List<Integer> listaLabelFracTemp = new ArrayList<>();
		listaLabelFracTemp = this.variaveisFracionadas.stream().map(i -> i.getLabel()).collect(Collectors.toList());
		List<Integer> listaLabelFracResTemp = new ArrayList<>();
		listaLabelFracResTemp = this.variaveisFracionadas.stream().map(i -> i.getLabel()).collect(Collectors.toList());
		listaLabelFracTemp.removeAll(listaLabelFracResTemp);
		boolean listasDiferentes = (listaLabelFracTemp.size() != 0);

		if (listasDiferentes) {
			return false;
		}

		List<Integer> listaLabelIntTemp = new ArrayList<>();
		listaLabelIntTemp = this.variaveisFracionadas.stream().map(i -> i.getLabel()).collect(Collectors.toList());
		List<Integer> listaLabelIntResTemp = new ArrayList<>();
		listaLabelIntResTemp = this.variaveisFracionadas.stream().map(i -> i.getLabel()).collect(Collectors.toList());
		listaLabelIntTemp.removeAll(listaLabelIntResTemp);
		listasDiferentes = (listaLabelIntTemp.size() != 0);

		if (listasDiferentes) {
			return false;
		}

		/*
		 * List<Item> listaTemp = new ArrayList<>();
		 * listaTemp.addAll(this.variaveisFracionadas);
		 * listaTemp.removeAll(resultado.getVariaveisFracionadas()); boolean
		 * listasDiferentes = (listaTemp.size() != 0);
		 * 
		 * if (listasDiferentes) { return false; }
		 */
		/*
		 * listaTemp = new ArrayList<>(); listaTemp.addAll(this.variaveisInteiras);
		 * listaTemp.removeAll(resultado.getVariaveisInteiras()); listasDiferentes =
		 * (listaTemp.size() != 0);
		 * 
		 * if (listasDiferentes) { return false; }
		 */

		return true;
	}

	public void imprimir() {
		System.out.println("\n\n******************* SOLUÇÃO FINAL ********************************");
		if (!this.valorFuncaoObjetivo.equals(-1D)) {
			StringBuilder builder = new StringBuilder();
			builder.append("\nValor da melhor solução: ");
			builder.append(this.valorFuncaoObjetivo);
			builder.append("\n");
			builder.append("Multi-conjunto: ");

			Collections.sort(this.variaveisInteiras, Item.porLabel());

			List<Item> variaveisInteirasNaoNulas = this.variaveisInteiras.stream().filter(i -> i.getX() != 0)
					.collect(Collectors.toList());

			String strItens = variaveisInteirasNaoNulas.stream()
					.map(item -> "m(S, " + item.getLabel() + ") = " + item.getX() + "")
					.collect(Collectors.joining(", "));

			builder.append(strItens);

			System.out.println(builder.toString());
		} else {
			System.out.println("\nSolução não encontrada.");
		}
	}
}
