package com.github.camelion.application;

import com.github.camelion.handlers.CommandHandler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.generics.BotOptions;
import org.telegram.telegrambots.generics.LongPollingBot;

import java.util.function.Consumer;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * @author Camelion
 * @since 10.03.17
 */
@Scope(SCOPE_PROTOTYPE)
@Component
final class TelegramLongPollingBot implements LongPollingBot, Consumer<Update>,
        ApplicationContextAware {

    private final String botUserName;
    private final String botToken;
    private final CommandHandler commandHandler;
    private ApplicationContext context;

    TelegramLongPollingBot(String botUserName, String botToken,
                           CommandHandler commandHandler) {
        this.botUserName = botUserName;
        this.botToken = botToken;
        this.commandHandler = commandHandler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        commandHandler.onUpdateReceived(update);
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public BotOptions getOptions() {
        return context.getBean(BotOptions.class);
    }

    @Override
    public void clearWebhook() throws TelegramApiRequestException {

    }

    @Override
    public void accept(Update update) {
        this.onUpdateReceived(update);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
