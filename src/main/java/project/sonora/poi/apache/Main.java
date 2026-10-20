package project.sonora.poi.apache;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        String nomeArquivo = "spotify-youtube-dataset_apache_poi.xlsx";
        Logger logger = new Logger();

        try {
            // 1. Leitura do Excel
            logger.info("Iniciando leitura do arquivo Excel: " + nomeArquivo);

            LeitorExcel leitor = new LeitorExcel();
            List<Musica> musicas = leitor.extrairMusica(nomeArquivo);

            System.out.println("Músicas extraídas: " + musicas.size());

            logger.sucesso(
                    "Leitura do Excel concluída. Músicas extraídas: "
                            + musicas.size()
            );

            // 2. Inserção no banco
            logger.info("Iniciando inserção das músicas no banco de dados");

            MusicaRepository repository = new MusicaRepository();
            int total = repository.salvarTodas(musicas);

            System.out.println("Total inserido no banco: " + total);

            logger.sucesso(
                    "Inserção concluída. Músicas inseridas: " + total
            );

            logger.sucesso("Execução da aplicação finalizada com sucesso");
        } catch (Exception e) {
            logger.erro(
                    "Erro durante a carga dos dados: " + e.getMessage()
            );

            System.err.println("Erro durante a carga dos dados: "
                    + e.getMessage());
            e.printStackTrace();
        }
    }
}