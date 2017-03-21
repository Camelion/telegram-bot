package com.github.camelion.command;

import com.github.camelion.service.DutyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.MessageEntity;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

/**
 * @author Camelion
 * @since 21.03.17
 * class for processing `/who` requests
 */
@Component
public class WhoCommandHandler implements ChatCommandHandler {

    private final DutyService dutyService;

    @Autowired
    public WhoCommandHandler(DutyService dutyService) {
        this.dutyService = dutyService;
    }

    @Override
    public void handleChatCommand(Message message, Flux<MessageEntity> messageEntities, AbsSender absSender) {
        String dutyName = dutyService.getDutyOnDate(LocalDateTime.now());

        try {
            SendMessage replyMessage = new SendMessage();
            replyMessage.setChatId(message.getChatId());
            replyMessage.setReplyToMessageId(message.getMessageId());
            replyMessage.setText("Дежурный на этой неделе: " + dutyName);

            absSender.sendMessage(replyMessage);
        } catch (TelegramApiException e) {
            ReflectionUtils.rethrowRuntimeException(e);
        }
    }
}
