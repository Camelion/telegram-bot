package com.github.camelion.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.telegram.telegrambots.ApiConstants;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.*;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.updateshandlers.SentCallback;

import java.io.Serializable;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * @author Camelion
 * @since 11.03.17
 */
@Component
@Scope(SCOPE_PROTOTYPE)
final class TelegramAbsSender extends AbsSender {
    private final String botToken;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    TelegramAbsSender(String botToken) {
        this.botToken = botToken;
    }

    @Override
    public Message sendDocument(SendDocument sendDocument) throws TelegramApiException {
        return null;
    }

    @Override
    public Message sendPhoto(SendPhoto sendPhoto) throws TelegramApiException {
        return null;
    }

    @Override
    public Message sendVideo(SendVideo sendVideo) throws TelegramApiException {
        return null;
    }

    @Override
    public Message sendSticker(SendSticker sendSticker) throws TelegramApiException {
        return null;
    }

    @Override
    public Message sendAudio(SendAudio sendAudio) throws TelegramApiException {
        return null;
    }

    @Override
    public Message sendVoice(SendVoice sendVoice) throws TelegramApiException {
        return null;
    }

    @Override
    protected <T extends Serializable, Method extends BotApiMethod<T>, Callback extends SentCallback<T>> void sendApiMethodAsync(Method method, Callback callback) {
    }

    @Override
    protected <T extends Serializable, Method extends BotApiMethod<T>> T sendApiMethod(Method method) throws TelegramApiException {
        method.validate();

        UriComponents uriComponents = UriComponentsBuilder.fromUriString(ApiConstants.BASE_URL)
                .path(botToken)
                .pathSegment(method.getMethod())
                .build();

        String response = restTemplate.postForObject(uriComponents.toUri(), method, String.class);

        return method.deserializeResponse(response);
    }
}
