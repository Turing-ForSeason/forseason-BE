package com.turing.forseason.global.mail;

import com.turing.forseason.global.errorException.CustomException;
import com.turing.forseason.global.errorException.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender emailSender;
    private final String alphaNumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final int codeLength = 6;

    public void sendEmail(String address, String title, String text) {
        SimpleMailMessage emailForm = createEmailForm(address, title, text);

        try {
            emailSender.send(emailForm);
        } catch (RuntimeException e) {
            throw new CustomException(ErrorCode.UNKNOWN_ERROR);
        }
    }

    public SimpleMailMessage createEmailForm(String address, String title, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(address);
        message.setSubject(title);
        message.setText(text);

        return message;
    }

    public String generateCode(){
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < codeLength; i++) {
            code.append(alphaNumeric.charAt(random.nextInt(alphaNumeric.length())));
        }

        return code.toString();
    }
}
