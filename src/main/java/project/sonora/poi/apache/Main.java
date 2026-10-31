package project.sonora.poi.apache;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        String nomeArquivo = "spotify-youtube-dataset_apache_poi.xlsx";
        Logger logger = new Logger();
        MusicaRepository repository = new MusicaRepository();

        try {
            if (args.length > 0
                    && args[0].equalsIgnoreCase("classificar")) {

                logger.info("Iniciando classificação dos gêneros dos artistas");

                int total = repository.classificarGenerosArtistas(2000);

                if (total > 0) {
                    logger.sucesso(
                            "Execução encerrada. Artistas processados: " + total
                    );
                } else {
                    logger.info(
                            "Nenhum artista foi processado nesta execução."
                    );
                }
                return;
            }

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