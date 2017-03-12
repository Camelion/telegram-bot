package com.github.camelion.handlers;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

/**
 * @author Camelion
 * @since 10.03.17
 */
public interface CommandHandler {
    void onUpdateReceived(Update update);

    default void setAbsSender(AbsSender absSender) {
        /* no-op */
    }
}
