package com.github.camelion.handlers;

import com.github.camelion.model.ChatMessage;
import com.github.camelion.repository.ElasticEnvironmentRepository;
import org.elasticsearch.action.percolate.PercolateResponse;
import org.elasticsearch.common.text.Text;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import reactor.core.publisher.Flux;

import java.io.IOException;
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
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ElasticEnvironmentRepository environmentRepository;

    private AbsSender absSender;

    @Autowired
    public YmqaBotCommandHandler(Logger logger, ApplicationContext context, ElasticsearchTemplate elasticsearchTemplate, ElasticEnvironmentRepository environmentRepository) {
        this.logger = logger;
        this.context = context;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.environmentRepository = environmentRepository;
    }

    public void setAbsSender(AbsSender absSender) {
        this.absSender = absSender;
    }

    @Override
    public void accept(Message incomingMessage) {
        // process chat commands
        Flux.just(incomingMessage.getText())
                .subscribe(text -> {
                    try {
                        processText(text, incomingMessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private void processText(String text, Message message) throws IOException {

        elasticsearchTemplate.putMapping(ChatMessage.class);
//        elasticsearchTemplate.deleteIndex(ChatMessage.class);
        elasticsearchTemplate.createIndex(ChatMessage.class);

        elasticsearchTemplate.getClient()
                .prepareIndex("messages", ".percolator", "Диван")
                .setSource("{ query: { match: { text: \"диван\"}}}")
                .setRefresh(true) // Needed when the query shall be available immediately
                .execute().actionGet();

        elasticsearchTemplate.getClient()
                .prepareIndex("messages", ".percolator", "Овощ")
                .setSource("{ query: { match: { text: \"овощ\"}}}")
                .setRefresh(true) // Needed when the query shall be available immediately
                .execute().actionGet();

        elasticsearchTemplate.getClient()
                .prepareIndex("messages", ".percolator", "Среда")
                .setSource("{ query: { match_phrase: { text: \"среда 1\"}}}")
                .setRefresh(true) // Needed when the query shall be available immediately
                .execute().actionGet();

        elasticsearchTemplate.getClient()
                .prepareIndex("messages", ".percolator", "Пятница")
                .setSource("{ query: { match: { text: \"пятница\"}}}")
                .setRefresh(true) // Needed when the query shall be available immediately
                .execute().actionGet();


        elasticsearchTemplate.getClient()
                .prepareIndex("messages", ".percolator", "Ticket")
                .setSource("{ query: { match_phrase: { text: \"сделайте тикет\"}}}")
                .setRefresh(true) // Needed when the query shall be available immediately
                .execute().actionGet();

        PercolateResponse response = elasticsearchTemplate.getClient().preparePercolate()
//                .setIndices("message")
                .setDocumentType("chatMessage")
                .setSource("{ doc: { text: \"" + text + "\"}}").execute().actionGet();

        System.out.println("start search for text: ");


        String outText = Arrays.stream(response.getMatches())
                .map(PercolateResponse.Match::getId).map(Text::string)
                .reduce((a, b) -> a + " и " + b).get();


        boolean ticket = false;
        for (PercolateResponse.Match match : response) {
            if (match.getId().string().equals("Ticket"))
                ticket = true;
            System.out.println("match:" + match);
        }

        System.out.println("done search for text: ");
        String componentText = "Нууу " + outText + " выглядит не очень, пойду посмотрю что там";

        String ticketText = null;


        if (ticket)
            ticketText = "Окей, я создал http://localhost:8080/ticket";

        try {
            SendMessage replyMessage = new SendMessage();
            replyMessage.setChatId(message.getChatId());
            if(ticket) {
                replyMessage.setReplyToMessageId(message.getMessageId());
            }
            replyMessage.setText(ticket ? ticketText : componentText);

            absSender.sendMessage(replyMessage);
        } catch (TelegramApiException e) {
            ReflectionUtils.rethrowRuntimeException(e);
        }
    }
}
