import br.com.TomaziTecnologies.TSScreen.Calculos.CalculadoraDeTempoJava;
import br.com.TomaziTecnologies.TSScreen.Calculos.FiltroRecomendacao;
import br.com.TomaziTecnologies.TSScreen.Modelos.*;

public class Main {
    public static void main(String[] args) {
        Film myFilm = new Film();
        myFilm.setNome("O Poderoso Chefão");
        myFilm.setDuracaoEmMinutos(210);
        myFilm.setIncluidoNoPlano(true);
        myFilm.setAnoDeLancamento(1972);
        System.out.println("Duração do filme: " + myFilm.getDuracaoEmMinutos());

        myFilm.exibeFichaTecnica();
        myFilm.avalia(9);
        myFilm.avalia(10);
        myFilm.avalia(10);
        System.out.println("Total de avaliações: " + myFilm.getTotalDeAvaliacoes());
        System.out.println(myFilm.obterMedia());

        Serie breakingbad = new Serie();
        breakingbad.setNome("Breaking Bad");
        breakingbad.exibeFichaTecnica();
        breakingbad.setTemporadas(6);
        breakingbad.setEpisodiosPorTemporada(12);
        breakingbad.setMinutosPorEpisodio(70);
        System.out.println("Duração para maratonar Breaking Bad:: " + breakingbad.getDuracaoEmMinutos());

        Film otherFilm = new Film();
        otherFilm.setNome("Django Livre");
        otherFilm.setDuracaoEmMinutos(180);
        otherFilm.setAnoDeLancamento(2012);

        CalculadoraDeTempoJava calculadora = new CalculadoraDeTempoJava();
        calculadora.inclui(myFilm);
        calculadora.inclui(otherFilm);
        calculadora.inclui(breakingbad);
        System.out.println(calculadora.getTempoTotal());

        FiltroRecomendacao filtro = new FiltroRecomendacao();
        filtro.filtra(myFilm);
    }
}
