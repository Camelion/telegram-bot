package com.github.camelion.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.TelegramBotsApi;

import java.util.List;

/**
 * @author Camelion
 * @since 10.03.17
 */
@Configuration
@ConfigurationProperties("telegram")
public class TelegramBotsConfiguration {
    private List<BotConfiguration> bots;

    public List<BotConfiguration> getBots() {
        return bots;
    }

    public void setBots(List<BotConfiguration> bots) {
        this.bots = bots;
    }

    public static class BotConfiguration {
        String name;
        String token;

        public String getBotCommandHandler() {
            return botCommandHandler;
        }

        public void setBotCommandHandler(String botCommandHandler) {
            this.botCommandHandler = botCommandHandler;
        }

        String botCommandHandler;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    @Bean
    TelegramBotsApi telegramBotsApi() {
        return new TelegramBotsApi();
    }
}
