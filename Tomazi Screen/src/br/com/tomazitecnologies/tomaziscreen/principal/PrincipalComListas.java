package br.com.tomazitecnologies.tomaziscreen.principal;
import br.com.tomazitecnologies.tomaziscreen.modelos.Filme;
import br.com.tomazitecnologies.tomaziscreen.modelos.Serie;
import br.com.tomazitecnologies.tomaziscreen.modelos.Titulo;

import java.util.*;

public class PrincipalComListas {
    public static void main(String[] args) {
        Filme meuFilme = new Filme("O Poderoso Chefão", 1972);
        meuFilme.avalia(10);
        Filme outroFilme = new Filme("Avatar",2023);
        outroFilme.avalia(7);
        var outroFilme2 = new Filme("Bastardos Inglórios", 2009);
        outroFilme2.avalia(10);
        Serie lost = new Serie("Lost", 2000);
        lost.avalia(7);

        Filme mf = meuFilme;

        ArrayList<Titulo> lista = new ArrayList<>();
        lista.add(outroFilme2);
        lista.add(outroFilme);
        lista.add(meuFilme);
        lista.add(lost);
        for (Titulo item: lista) {
            System.out.println(item.getNome());
            if (item instanceof Filme filme && filme.getClassificacao() > 2){
                System.out.println("Classificação: " + filme.getClassificacao());
            }
        }
        List<String> buscaPorArtista = new LinkedList<>();
        buscaPorArtista.add("Brad Pitt");
        buscaPorArtista.add("Leonardo Dicaprio");
        buscaPorArtista.add("Jamie Foxx");
        buscaPorArtista.add("Adam Sandler");
        System.out.println(buscaPorArtista);

        Collections.sort(buscaPorArtista);
        System.out.println("Depois da ordenação");
        System.out.println(buscaPorArtista);
        System.out.println("Lista de filmes ordenados");
        Collections.sort(lista);
        System.out.println(lista);
        Collections.sort(lista);
        lista.sort(Comparator.comparing(Titulo::getAnoDeLancamento));
        System.out.println("Ordenando por ano de lançamento");
        System.out.println(lista);
    }
}
