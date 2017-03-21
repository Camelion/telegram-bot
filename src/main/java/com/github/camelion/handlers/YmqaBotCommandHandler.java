package com.github.camelion.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.MessageEntity;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

/**
 * @author Camelion
 * @since 21.03.17
 */
@Component
public class YmqaBotCommandHandler implements BotCommandHandler {
    private static final List<String> ALLOWED_COMMANDS =
            Arrays.asList("who");

    private AbsSender absSender;

    public void setAbsSender(AbsSender absSender) {
        this.absSender = absSender;
    }

    @Override
    public void accept(Message incomingMessage) {
        Flux.fromIterable(incomingMessage.getEntities())
                .groupBy(MessageEntity::getText)
                .filter(gf -> ALLOWED_COMMANDS.contains(gf.key()));
        try {
            SendMessage message = new SendMessage();
            message.setChatId(incomingMessage.getChatId());
            message.setReplyToMessageId(incomingMessage.getMessageId());
            message.setText("Привет!");

            absSender.sendMessage(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
