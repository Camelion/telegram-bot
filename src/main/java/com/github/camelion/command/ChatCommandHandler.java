package com.github.camelion.command;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.MessageEntity;
import org.telegram.telegrambots.bots.AbsSender;
import reactor.core.publisher.Flux;

/**
 * @author Camelion
 * @since 21.03.17
 */
public interface ChatCommandHandler {
    void handleChatCommand(Message message, Flux<MessageEntity> messageEntities, AbsSender absSender);
}
