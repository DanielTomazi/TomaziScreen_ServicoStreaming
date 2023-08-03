import br.com.TomaziTecnologies.TSScreen.Modelos.*;

public class Main {
    public static void main(String[] args) {
        Film myFilm = new Film();
        myFilm.setNome("O Poderoso Chefão");
        myFilm.setDuracaoEmMinutos(210);
        myFilm.setIncluidoNoPlano(true);
        myFilm.setAnoDeLancamento(1972);

        myFilm.exibeFichaTecnica();
        myFilm.avalia(9);
        myFilm.avalia(10);
        myFilm.avalia(10);
        System.out.println("Total de avaliações: " + myFilm.getTotalDeAvaliacoes());
        System.out.println(myFilm.obterMedia());
    }
}
