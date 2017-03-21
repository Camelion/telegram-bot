package com.github.camelion.handlers;

import com.github.camelion.command.ChatCommandHandler;
import com.github.camelion.command.WhoCommandHandler;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.MessageEntity;
import org.telegram.telegrambots.bots.AbsSender;
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
            Arrays.asList("/who");

    private final Logger logger;
    private final ApplicationContext context;

    private AbsSender absSender;

    @Autowired
    public YmqaBotCommandHandler(Logger logger, ApplicationContext context) {
        this.logger = logger;
        this.context = context;
    }

    public void setAbsSender(AbsSender absSender) {
        this.absSender = absSender;
    }

    @Override
    public void accept(Message incomingMessage) {
        // process chat commands
        Flux.fromIterable(incomingMessage.getEntities())
                .groupBy(MessageEntity::getText)
                .filter(gf -> ALLOWED_COMMANDS.contains(gf.key()))
                .subscribe(flux -> processChatCommands(flux.key(), flux, incomingMessage));
    }

    private void processChatCommands(String key, Flux<MessageEntity> messageEntities, Message message) {
        ChatCommandHandler chatCommandHandler;

        switch (key) {
            case "/who":
                chatCommandHandler = context.getBean(WhoCommandHandler.class);
                break;
            default:
                logger.debug("There are no ChatCommandHandler for command [{}]", key);
                return;
        }

        Assert.notNull(chatCommandHandler, "ChatCommandHandler should be defined for command [" + key + "]");

        chatCommandHandler.handleChatCommand(message, messageEntities, absSender);
    }
}
