package com.nguyenthanhnhan.backendshopcaulong.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "drpo7kn7k",
                "api_key", "373739124361454",
                "api_secret", "6O0iezeys0wmHvSS4Z5A1AY9YjQ",
                "secure", true));
    }
}