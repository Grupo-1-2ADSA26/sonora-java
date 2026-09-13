package project.sonora;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class JavaMail {
    public static void main(String[] args) {
        String remetente = "mauroneto4321@gmail.com";
        String senha = System.getenv("senhaApp");
        String destinatario = "mauro.camposno@sptech.school";

        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(
                props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                remetente,
                                senha
                        );
                    }
                }
        );

        try {
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(remetente));

            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(destinatario)
            );

            message.setSubject("Teste JavaMail");

            message.setText(
                    "Olá!\n\n"
                            + "Este e-mail foi enviado utilizando JavaMail."
            );

            Transport.send(message);

            System.out.println("E-mail enviado com sucesso!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
