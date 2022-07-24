package com.nhnacademy.marketgg.auth.util;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MailUtils {

    @Value("${mail.username}")
    private String fromEmail;

    @Value("${mail.password}")
    private String fromEmailPassword;

    @Value("${mail.host}")
    private String toHost;

    @Value("${mail.port}")
    private String toPort;

    private final Session session;

    public MailUtils() {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", 465);
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        prop.put("mail.smtp.starttls.enable", "true");

        this.session = Session.getDefaultInstance(prop, new Authenticator() {

            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, fromEmailPassword);
            }

        });
    }

    public boolean sendMail(String email) {
        try {

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("[Market GG] 인증코드 전송");

            message.setContent(
                "<h1>[이메일 인증]</h1> <p>아래 버튼을 클릭하시면 이메일 인증이 완료됩니다.</p> " +
                    "<form action=\"https://\"" + toHost + ":\"" + toPort +
                    "/auth/use/email/use?use=true\" method=\"post\">\n" +
                    "<input type=\"hidden\"" + "id=\"email\" value=\"" + email + "\">" +
                    "    <button type=\"submit\">인증하기</button>\n" +
                    "</form>"
                , "text/html;charset=euc-kr"
            );

            Transport.send(message);

        } catch (Exception ex) {
            log.error("", ex);

            return false;
        }

        return true;

    }

}
