package com.github.camelion.application;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.telegram.telegrambots.ApiConstants;
import org.telegram.telegrambots.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.generics.BotOptions;
import org.telegram.telegrambots.generics.BotSession;
import org.telegram.telegrambots.generics.LongPollingBot;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author Camelion
 * @since 10.03.17
 */
public class TelegramBotSession implements BotSession {
    private TelegramLongPollingBot botCallback;
    private Disposable sessionFluxDisposable;
    private String token;
    private TelegramBotOptions botOptions;
    private volatile int lastUpdateId = 0;

    @Override
    public void setOptions(BotOptions options) {
        Assert.isInstanceOf(TelegramBotOptions.class, options,
                "Options should be an instance of TelegramBotOptions");

        this.botOptions = (TelegramBotOptions) options;
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void setCallback(LongPollingBot callback) {
        Assert.isInstanceOf(TelegramLongPollingBot.class, callback,
                "Callback should be an instance of TelegramLongPollingBot");

        this.botCallback = (TelegramLongPollingBot) callback;
    }

    @Override
    public void start() {
        Supplier<List<Update>> supplier = () -> {
            GetUpdates request = new GetUpdates()
                    .setLimit(100)
                    .setTimeout(ApiConstants.GETUPDATES_TIMEOUT)
                    .setOffset(lastUpdateId + 1);

            String url = ApiConstants.BASE_URL + token + "/" + GetUpdates.PATH;
            String response = botOptions.getRestTemplate().postForObject(
                    url,
                    request,
                    String.class);
            try {
                return request.deserializeResponse(response);
            } catch (TelegramApiRequestException e) {
                ReflectionUtils.rethrowRuntimeException(e);
            }

            return Collections.emptyList();
        };

        sessionFluxDisposable =
                Mono.fromSupplier(supplier)
                        .doOnNext(updates -> updates.sort(Comparator.comparing(Update::getUpdateId)))
                        .repeat()
                        .flatMap(Flux::fromIterable)
                        .doOnNext(update -> lastUpdateId = update.getUpdateId())
                        .filter(update -> {
                            ZonedDateTime msgTime = ZonedDateTime.ofInstant(
                                    Instant.ofEpochSecond(
                                            update.getMessage().getDate()),
                                    ZoneOffset.UTC);
                            ZonedDateTime botTime = ZonedDateTime.now(ZoneOffset.UTC)
                                    .truncatedTo(ChronoUnit.SECONDS);

                            return botTime.minusSeconds(30)
                                    .isBefore(msgTime);
                        })
                        .subscribeOn(Schedulers.elastic())
                        .subscribe(botCallback);
    }

    @Override
    public void stop() {
        sessionFluxDisposable.dispose();
    }

    @Override
    public boolean isRunning() {
        return !sessionFluxDisposable.isDisposed();
    }
}
