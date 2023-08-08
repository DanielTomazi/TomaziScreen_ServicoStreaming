package br.com.TomaziTecnologies.TSScreen.Calculos;
import br.com.TomaziTecnologies.TSScreen.Modelos.Titulo;

public class CalculadoraDeTempoJava {
    private int tempoTotal;

    public int getTempoTotal() {
        return tempoTotal;
    }

    public void setTempoTotal(int tempoTotal) {
        this.tempoTotal = tempoTotal;
    }
//    public void inclui(Film f){
//        this.tempoTotal += f.getDuracaoEmMinutos();
//    }
//    public void inclui(Serie s) {
//        this.tempoTotal += s.getDuracaoEmMinutos();
//    }

    public void inclui(Titulo titulo){
        System.out.println("Adicionando duração em minutos de " + titulo);
        this.tempoTotal = titulo.getDuracaoEmMinutos();
    }
}

