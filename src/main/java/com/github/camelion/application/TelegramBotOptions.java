package com.github.camelion.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.generics.BotOptions;

import java.io.Serializable;

/**
 * @author Camelion
 * @since 10.03.17
 */
@Component
class TelegramBotOptions implements BotOptions, Serializable {

    private static final long serialVersionUID = 704676633273243633L;
    private final RestTemplate restTemplate;

    @Autowired
    TelegramBotOptions(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
}
