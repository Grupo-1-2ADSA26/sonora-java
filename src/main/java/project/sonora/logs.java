package project.sonora;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class logs {
    public static void main(String[] args) {
        Integer sair = null;
        LocalDateTime dataHora = null;
        DateTimeFormatter formatoData = null;
        String dataHoraFormatada = null;
        formatoData = formatoData.ofPattern("dd/MM/yyyy HH:mm:ss");
        List<String> logs = new ArrayList<>();
        Scanner scan = new Scanner(System.in);
        Scanner scanLn = new Scanner(System.in);
        Integer opcao = null;

        String menu = """         
               ========================================
               |                                      |
               |     ***Seja bem vindo ao Sonora***   |
               |                                      |
               |       Selecione a opção desejada     |
               |         1- Cadastro de Usuário       |
               |         2- Cadastro de Empresa       |
               |         3- Sugestão de Artista       |
               |         4- Consultar Logs            |
               |         5- Sair                      |
               |                                      |
               ========================================            
                """;

        while(true) {
            System.out.println(menu);
            opcao = scan.nextInt();
            switch (opcao){
                case 1:
                        String nome,email,senha = null;

                        System.out.println("Digite seu nome");
                        nome = scanLn.nextLine();

                        System.out.println("Digite seu email");
                        email = scanLn.nextLine();

                        System.out.println("Digite sua senha");
                        senha = scanLn.nextLine();

                    dataHora = LocalDateTime.now();
                    dataHoraFormatada = dataHora.format(formatoData);
                    logs.add("O usuário "+nome+" foi adicionado com sucesso ao sistema  "+dataHoraFormatada);
                break;

                case 2:
                    String razaoSocial,endereco,cnpj = null;

                    System.out.println("Digite a razão social");
                    razaoSocial = scanLn.nextLine();

                    System.out.println("Digite o endereço da empresa");
                    endereco = scanLn.nextLine();

                    do {
                        System.out.println("Digite o CNPJ da empresa");
                        cnpj = scanLn.nextLine();
                    }while (cnpj.length() != 14);

                    dataHora = LocalDateTime.now();
                    dataHoraFormatada = dataHora.format(formatoData);
                    logs.add("A empresa "+razaoSocial+" foi adicionada com sucesso ao sistema  "+dataHoraFormatada);
                break;

                case 3:
                    Random rand = new Random();
                    List<String> artistas = new ArrayList<>();
                    artistas.add("""
                           Nome: Bruno Mars
                           Gênero: Pop
                           Views: 2B
                            """);
                    artistas.add("""
                           Nome: Scalene
                           Gênero: Rock
                           Views: 20M
                            """);
                    artistas.add("""
                           Nome: Bruno e Marrone
                           Gênero: Sertanejo
                           Views: 200M
                            """);
                    artistas.add("""
                           Nome: Linkin park
                           Gênero: Rock
                           Views: 4B
                            """);
                    artistas.add("""
                           Nome: Sabrina Carpenter
                           Gênero: Pop
                           Views: 7B
                            """);
                    artistas.add("""
                           Nome: Twice
                           Gênero: K-Pop
                           Views: 6B
                            """);
                    Integer num = rand.nextInt(6);

                    do{
                        System.out.println(artistas.get(num));
                        System.out.println("Se deseja sair digite 0");
                        sair = scan.nextInt();
                    }while (sair != 0);

                    dataHora = LocalDateTime.now();
                    dataHoraFormatada = dataHora.format(formatoData);
                    logs.add("A sugestão foi feita com sucesso pelo sistema  "+dataHoraFormatada);
                break;
                case 4:

                    String retorno = "";
                    for (int i = 0; i < logs.size(); i++) {
                        retorno += logs.get(i)+"\n";
                    }
                    do{
                        System.out.println(retorno);
                        System.out.println("Se deseja sair digite 0");
                        sair = scan.nextInt();
                    }while (sair != 0);

                break;

                case 5:
                return;

                default:
                    System.out.println("Digite uma opção válida");
                break;
            }
        }
    }
}
