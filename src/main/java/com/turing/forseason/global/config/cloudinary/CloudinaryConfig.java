package com.turing.forseason.global.config.cloudinary;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        // Cloudinary 인스턴스 생성
        Cloudinary cloudinary = new Cloudinary();

        // Cloudinary 설정 (cloud_name, api_key, api_secret)
        cloudinary.config.cloudName = "forseason";
        cloudinary.config.apiKey = "785557441413631";
        cloudinary.config.apiSecret = "hyLDllqGUHPeCNnTZWTGvPfzE6U";

        return cloudinary;
    }
}