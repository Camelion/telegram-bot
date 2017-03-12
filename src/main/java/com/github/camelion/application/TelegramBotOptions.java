package com.github.camelion.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.generics.BotOptions;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * @author Camelion
 * @since 10.03.17
 */
@Component
@Scope(SCOPE_PROTOTYPE)
class TelegramBotOptions implements BotOptions {
    private final RestTemplate restTemplate;

    @Autowired
    TelegramBotOptions(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
}
