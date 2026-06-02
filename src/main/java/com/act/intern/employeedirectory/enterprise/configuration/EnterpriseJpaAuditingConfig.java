package com.act.intern.employeedirectory.enterprise.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "enterpriseAuditorProvider")
public class EnterpriseJpaAuditingConfig {

    @Bean(name = "enterpriseAuditorProvider")
    @ConditionalOnMissingBean(name = "enterpriseAuditorProvider")
    public AuditorAware<String> auditorProvider() {
        // Replace with SecurityContextHolder.getContext().getAuthentication().getName() in production
        return () -> Optional.of("system");
    }
}
