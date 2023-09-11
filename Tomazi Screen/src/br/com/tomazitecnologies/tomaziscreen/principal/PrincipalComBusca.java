package br.com.tomazitecnologies.tomaziscreen.principal;

import br.com.tomazitecnologies.tomaziscreen.modelos.Titulo;
import br.com.tomazitecnologies.tomaziscreen.modelos.TituloOmdb;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class PrincipalComBusca {
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner leitura = new Scanner(System.in);
        System.out.println("Digite um filme para busca: ");
        var busca = leitura.nextLine();
        String endereco = "https://www.omdbapi.com/?t=" + busca + "&APIKey=9c3ba3da";
    try {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endereco))
                .build();
        HttpResponse<String> response = client
                .send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        String json = response.body();
        System.out.println(json);

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();
        TituloOmdb meuTituloOmdb = gson.fromJson(json, TituloOmdb.class);
        System.out.println(meuTituloOmdb);

            Titulo meuTitulo = new Titulo(meuTituloOmdb);
            System.out.println("Titulo já convertido: ");
            System.out.println(meuTitulo);
        } catch (NumberFormatException e) {
        System.out.println("Aconteceu um erro: ");
        System.out.println(e.getMessage());
    } catch (IllegalArgumentException e){
        System.out.println("Algum erro de argumento na busca, verifique o endereço.");
    }
        System.out.println("O programa finalizou corretamente!");
    }
}
