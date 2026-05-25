package com.example.demo.configuration;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.example.demo.service.DiscordService;

@SpringBootConfiguration
@EnableConfigurationProperties
@ComponentScan(basePackageClasses = {
        DiscordService.class
})
@Import({
        DiscordConfiguration.class
})
public class JavaFxSpringConfiguration {
}