package com.turing.forseason.global.config.cloudinary;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Cloudinary cloudinary = new Cloudinary();

        // Cloudinary 계정 정보 설정
        cloudinary.config.cloudName = "YOUR_CLOUD_NAME";
        cloudinary.config.apiKey = "YOUR_API_KEY";
        cloudinary.config.apiSecret = "YOUR_API_SECRET";

        return cloudinary;
    }
}



