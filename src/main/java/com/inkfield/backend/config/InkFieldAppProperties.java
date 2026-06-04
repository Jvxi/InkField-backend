package com.inkfield.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "inkfield.app")
public class InkFieldAppProperties {
    private String publicUrl = "http://localhost:5173";

    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    public String baseUrl() {
        String base = publicUrl == null ? "" : publicUrl.trim();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base.isBlank() ? "http://localhost:5173" : base;
    }

    public String registerUrl() {
        return baseUrl() + "/register";
    }
}