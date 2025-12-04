package com.manga;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MangaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MangaServerApplication.class, args);
    }
}


