package com.atlasfs.storagenode.config;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    @Bean
    public Path storageDirectory() throws IOException {
        Path path = Paths.get("data", "chunks").toAbsolutePath();

        return path;
    }
}