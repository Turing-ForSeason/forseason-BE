package com.turing.forseason.global.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender emailSender;
    private final String alphaNumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final int codeLength = 6;

    public void sendEmail(String address, String title, String text) throws Throwable {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");


        helper.setTo(address);
        helper.setSubject(title);
        helper.setText(text, true);

        emailSender.send(message);
    }


    public String generateCode(){
        // 인증 코드 만들기
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < codeLength; i++) {
            code.append(alphaNumeric.charAt(random.nextInt(alphaNumeric.length())));
        }

        return code.toString();
    }
}
