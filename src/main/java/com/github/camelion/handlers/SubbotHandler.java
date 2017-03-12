package com.github.camelion.handlers;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * @author Camelion
 * @since 10.03.17
 */
@Component
public class SubbotHandler implements CommandHandler {
    private Logger logger;
    private AbsSender absSender;

    @Autowired
    public SubbotHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void onUpdateReceived(Update update) {
        logger.info("update received {}", update);

        try {
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId());
            message.setReplyToMessageId(update.getMessage().getMessageId());
            message.setText("Привет!");

            absSender.sendMessage(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setAbsSender(AbsSender absSender) {
        this.absSender = absSender;
    }
}
