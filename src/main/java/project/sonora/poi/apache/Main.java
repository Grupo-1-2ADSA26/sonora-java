package project.sonora.poi.apache;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String nomeArquivo = "spotify-youtube-dataset_apache_poi-mod.xlsx";

        // Extraindo os livros do arquivo
        LeitorExcel leitorExcel = new LeitorExcel();
        List<Musica> musicasExtraidas = leitorExcel.extrairMusica(nomeArquivo);

        System.out.println("Livros extraídos:");
        for (Musica musica : musicasExtraidas) {
            System.out.println(musica);
        }
    }
}
