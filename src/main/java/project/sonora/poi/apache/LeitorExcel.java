package project.sonora.poi.apache;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.CellType;

public class LeitorExcel {
    private boolean celulaValida(Row row, int coluna) {
        if (row.getCell(coluna) == null) {
            return false;
        }

        CellType tipo = row.getCell(coluna).getCellType();

        if (tipo == CellType.BLANK || tipo == CellType.ERROR) {
            return false;
        }

        if (tipo == CellType.FORMULA) {
            return row.getCell(coluna).getCachedFormulaResultType() != CellType.ERROR;
        }

        return true;
    }

    public List<Musica> extrairMusica(String nomeArquivo) {
        List<Musica> musicasExtraidas = new ArrayList<>();

        try (
                InputStream arquivo = new FileInputStream(nomeArquivo);
                Workbook workbook = new XSSFWorkbook(arquivo)
        ) {

            System.out.printf("Iniciando leitura do arquivo %s%n", nomeArquivo);

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    printarCabecalho(row);
                    continue;
                }

                // Extraindo valor das células e criando objeto Livro
                System.out.println("Lendo linha " + row.getRowNum());

                Integer idMusica = null;
                if (celulaValida(row, 0) && row.getCell(0).getCellType() == CellType.NUMERIC) {
                    idMusica = (int) row.getCell(0).getNumericCellValue();
                }

                String nomeArtista = null;
                if (celulaValida(row, 1)) {
                    nomeArtista = row.getCell(1).getStringCellValue();
                }

                String urlSpotify = null;
                if (celulaValida(row, 2)) {
                    urlSpotify = row.getCell(2).getStringCellValue();
                }

                String trackMusica = null;
                if (celulaValida(row, 3)) {
                    trackMusica = row.getCell(3).getStringCellValue();
                }

                String album = null;
                if (celulaValida(row, 4)) {
                    album = row.getCell(4).getStringCellValue();
                }

                String albumTipo = null;
                if (celulaValida(row, 5)) {
                    albumTipo = row.getCell(5).getStringCellValue();
                }

                Double danceability = null;
                if (celulaValida(row, 7) && row.getCell(7).getCellType() == CellType.NUMERIC) {
                    danceability = row.getCell(7).getNumericCellValue();
                }

                Double energy = null;
                if (celulaValida(row, 8) && row.getCell(8).getCellType() == CellType.NUMERIC) {
                    energy = row.getCell(8).getNumericCellValue();
                }

                Double loudness = null;
                if (celulaValida(row, 10) && row.getCell(10).getCellType() == CellType.NUMERIC) {
                    loudness = row.getCell(10).getNumericCellValue();
                }

                Double speechiness = null;
                if (celulaValida(row, 11) && row.getCell(11).getCellType() == CellType.NUMERIC) {
                    speechiness = row.getCell(11).getNumericCellValue();
                }

                Double acousticness = null;
                if (celulaValida(row, 12) && row.getCell(12).getCellType() == CellType.NUMERIC) {
                    acousticness = row.getCell(12).getNumericCellValue();
                }

                Double valence = null;
                if (celulaValida(row, 15) && row.getCell(15).getCellType() == CellType.NUMERIC) {
                    valence = row.getCell(15).getNumericCellValue();
                }

                Double tempo = null;
                if (celulaValida(row, 16) && row.getCell(16).getCellType() == CellType.NUMERIC) {
                    tempo = row.getCell(16).getNumericCellValue();
                }

                Double durationMs = null;
                if (celulaValida(row, 17) && row.getCell(17).getCellType() == CellType.NUMERIC) {
                    durationMs = row.getCell(17).getNumericCellValue();
                }

                String titleMusica = null;
                if (celulaValida(row, 19)) {
                    titleMusica = row.getCell(19).getStringCellValue();
                }

                Integer views = null;
                if (celulaValida(row, 21) && row.getCell(21).getCellType() == CellType.NUMERIC) {
                    views = (int) row.getCell(21).getNumericCellValue();
                }

                Integer likes = null;
                if (celulaValida(row, 22) && row.getCell(22).getCellType() == CellType.NUMERIC) {
                    likes = (int) row.getCell(22).getNumericCellValue();
                }

                Integer comentarios = null;
                if (celulaValida(row, 23) && row.getCell(23).getCellType() == CellType.NUMERIC) {
                    comentarios = (int) row.getCell(23).getNumericCellValue();
                }

                Integer streams = null;
                if (celulaValida(row, 27) && row.getCell(27).getCellType() == CellType.NUMERIC) {
                    streams = (int) row.getCell(27).getNumericCellValue();
                }

                Musica musica = new Musica(idMusica,nomeArtista,urlSpotify,trackMusica,album,albumTipo,
                        danceability,energy,loudness,speechiness,acousticness,valence,tempo,durationMs,
                        titleMusica,views,likes,comentarios,streams);

                musicasExtraidas.add(musica);
            }

            printarLinhas();
            System.out.println("Leitura do arquivo finalizada");
            printarLinhas();

            return musicasExtraidas;
        } catch (IOException e) {
            return musicasExtraidas;
        }
    }

    private void printarCabecalho(Row row) {
        printarLinhas();
        System.out.println("Lendo cabeçalho");
        for (int i = 0; i < 4; i++) {
            String coluna = row.getCell(i).getStringCellValue();
            System.out.println("Coluna " + i + ": " + coluna);
        }
        printarLinhas();
    }

    private void printarLinhas() {
        System.out.println("-".repeat(20));
    }
}
