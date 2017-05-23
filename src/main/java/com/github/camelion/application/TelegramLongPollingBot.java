package com.github.camelion.application;

import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.generics.BotOptions;
import org.telegram.telegrambots.generics.LongPollingBot;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

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

    private static final Scheduler BOTCOMMAND_SCHEDULER =
            Schedulers.newElastic("botcommands");

    private final String botUserName;
    private final String botToken;
    private final Consumer<Message> botCommandHandler;
    private ApplicationContext context;
    private Logger logger;

    TelegramLongPollingBot(String botUserName, String botToken,
                           Consumer<Message> botCommandHandler) {
        this.botUserName = botUserName;
        this.botToken = botToken;
        this.botCommandHandler = botCommandHandler;
    }

    @Autowired
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void accept(Update update) {
        if (!update.hasMessage()) {
            logger.trace("Ignored update without messages, {}", update);
            return;
        }

        this.onUpdateReceived(update);
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        // process message with entities
        Flux.just(message)
                .subscribeOn(BOTCOMMAND_SCHEDULER)
                .subscribe(botCommandHandler);
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
        /* no-op */
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
