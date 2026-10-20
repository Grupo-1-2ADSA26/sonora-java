package project.sonora;

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

        consulta.exibirLogs();
    }
}
