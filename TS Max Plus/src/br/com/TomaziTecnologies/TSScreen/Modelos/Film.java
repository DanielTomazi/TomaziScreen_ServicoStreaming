package br.com.TomaziTecnologies.TSScreen.Modelos;

import br.com.TomaziTecnologies.TSScreen.Calculos.Classificavel;

public class Film extends Titulo implements Classificavel {
    private String diretor;

    public String getDiretor() {
        return diretor;
    }

    public void setDiretor(String diretor) {
        this.diretor = diretor;
    }

    @Override
    public int getClassificacao() {
        return (int) obterMedia() / 2;
    }
}


