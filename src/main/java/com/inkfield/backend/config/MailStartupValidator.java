package com.inkfield.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class MailStartupValidator implements ApplicationRunner {
    private final InkFieldMailProperties mailProperties;
    private final String mailHost;

    public MailStartupValidator(
        InkFieldMailProperties mailProperties,
        @Value("${spring.mail.host:}") String mailHost
    ) {
        this.mailProperties = mailProperties;
        this.mailHost = mailHost == null ? "" : mailHost.trim();
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!mailProperties.isRequired()) {
            return;
        }
        if (mailHost.isBlank()) {
            throw new IllegalStateException(
                "inkfield.mail.required=true 但未配置 MAIL_HOST / spring.mail.host，无法发送注册验证码邮件。"
            );
        }
    }
}
