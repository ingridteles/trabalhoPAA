package mit;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Entrada {
	public static int n;
	public static Double W1;
	public static Double W2;
	public static Double W3;
	public static List<Item> itens;

	// Ler dados do arquivo da instância
	public static void lerEntrada(String path) {
		try {
			String[] linha = null;
			Scanner entrada = new Scanner(new BufferedReader(new FileReader(path)));
			linha = recuperarLinha(entrada);
			n = Integer.parseInt(linha[0]);
			linha = recuperarLinha(entrada);
			W1 = Double.parseDouble(linha[0]);
			W2 = Double.parseDouble(linha[1]);
			W3 = Double.parseDouble(linha[2]);

			itens = new LinkedList<Item>();
			// Lê o restante das linhas e cria os itens
			for (int i = 0; i < n; i++) {
				linha = recuperarLinha(entrada);
				Item item = new Item(i, Double.parseDouble(linha[0]), Double.parseDouble(linha[1]),
						Double.parseDouble(linha[2]), Double.parseDouble(linha[3]));
				itens.add(item);
			}

			// Ordena os itens por custo benefício decrescente
			// TODO ajeitar esssa ordenação
			//Collections.sort(itens, Item.porCustoBeneficio());
			System.out.println(itens.stream().map(item -> String.valueOf(item.getValor())).collect(Collectors.joining(", ", "\nItem Por Custo Beneficio: [", "]")));
			System.err.println("");

		} catch (FileNotFoundException e) {
			System.out.printf("\n Arquivo: \"%s\" não encontrado.", path);
			System.exit(0);
		}
	}

	private static String[] recuperarLinha(Scanner entrada) {
		String linha = entrada.nextLine();
		linha = removerEspacosInicoFim(linha);
		String[] substr = linha.split(("\\s+"));
		return substr;
	}

	private static String removerEspacosInicoFim(String linha) {
		linha = linha.replaceAll("^\\s+", "");
		return linha.replaceAll("\\s+$", "");
	}
}
