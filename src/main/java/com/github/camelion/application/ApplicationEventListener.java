package com.github.camelion.application;

import com.github.camelion.configuration.TelegramBotsConfiguration;
import com.github.camelion.configuration.TelegramBotsConfiguration.BotConfiguration;
import com.github.camelion.handlers.CommandHandler;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.telegram.telegrambots.ApiContext;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.generics.BotSession;
import org.telegram.telegrambots.generics.LongPollingBot;

/**
 * @author Camelion
 * @since 10.03.17
 */
@Configuration
class ApplicationEventListener implements ApplicationListener<ContextRefreshedEvent> {

    private final TelegramBotsConfiguration botsConfiguration;
    private final TelegramBotsApi telegramBotsApi;
    private final Logger logger;

    @Autowired
    ApplicationEventListener(TelegramBotsConfiguration botsConfiguration,
                             TelegramBotsApi telegramBotsApi,
                             Logger logger) {
        this.botsConfiguration = botsConfiguration;
        this.telegramBotsApi = telegramBotsApi;
        this.logger = logger;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApiContext.register(BotSession.class, TelegramBotSession.class);

        ApplicationContext context = event.getApplicationContext();
        for (BotConfiguration botConfiguration : botsConfiguration.getBots()) {
            AbsSender absSender = context.getBean(TelegramAbsSender.class, botConfiguration.getToken());

            CommandHandler commandHandler = context.getBean(botConfiguration.getHandlerBean(), CommandHandler.class);
            commandHandler.setAbsSender(absSender);

            LongPollingBot bot = context.getBean(TelegramLongPollingBot.class,
                    botConfiguration.getName(), botConfiguration.getToken(), commandHandler);

            try {
                telegramBotsApi.registerBot(bot);
            } catch (TelegramApiRequestException e) {
                logger.error(e.getMessage(), e);
            }

            logger.info("Registered bot [{}] with token: *****", botConfiguration.getName());
        }
    }
}
