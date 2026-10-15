package project.sonora.poi.apache;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        String nomeArquivo = "spotify-youtube-dataset_apache_poi.xlsx";

        try {
            // 1. Leitura do Excel
            LeitorExcel leitor = new LeitorExcel();
            List<Musica> musicas = leitor.extrairMusica(nomeArquivo);

            System.out.println("Músicas extraídas: " + musicas.size());

            // 2. Inserção no banco
            MusicaRepository repository = new MusicaRepository();
            int total = repository.salvarTodas(musicas);

            System.out.println("Total inserido no banco: " + total);

        } catch (Exception e) {
            System.err.println("Erro durante a carga dos dados: "
                    + e.getMessage());
            e.printStackTrace();
        }
    }
}