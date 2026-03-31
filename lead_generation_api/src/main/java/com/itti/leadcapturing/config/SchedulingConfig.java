package com.itti.leadcapturing.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling  // ← Add this
public class SchedulingConfig {
    // Enables @Scheduled annotations
}