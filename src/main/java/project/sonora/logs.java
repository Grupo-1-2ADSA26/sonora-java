package project.sonora;

import project.sonora.poi.apache.CorrigirTextoUtil;
import project.sonora.poi.apache.LogConsulta;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class logs {
    public static void main(String[] args) {
        LogConsulta consulta = new LogConsulta();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Menu =====");
            System.out.println("1 - Ver logs");
            System.out.println("2 - Sair");
            System.out.print("Escolha uma opção: ");

            int opcao;
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida!");
                return;
            }

            switch (opcao) {
                case 1:
                    consulta.exibirLogs();
                    break;

                case 2:
                    return;
                default:
                    System.out.println("Opção Inválida");
                    break;
            }
        }
    }
}
