package com.github.camelion.handlers;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.function.Consumer;

/**
 * @author Camelion
 * @since 10.03.17
 */
public interface BotCommandHandler extends Consumer<Message> {
    void setAbsSender(AbsSender absSender);
}
