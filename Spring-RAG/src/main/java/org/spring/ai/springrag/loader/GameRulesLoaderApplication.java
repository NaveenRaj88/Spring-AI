package org.spring.ai.springrag.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.ai.springrag.model.GameTitle;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class GameRulesLoaderApplication {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GameRulesLoaderApplication.class);

    @Value("classpath:/promptTemplates/nameOfTheGame.st")
    Resource nameOfTheGameTemplateResource;

    @Bean
    Function<Flux<List<Document>>, Flux<List<Document>>>
    titleDeterminer(ChatClient.Builder chatClientBuilder) {

        var chatClient = chatClientBuilder.build();

        return documentListFlux -> documentListFlux
                .map(documents -> {
                    if (!documents.isEmpty()) {
                        var firstDocument = documents.getFirst();

                        var gameTitle = chatClient.prompt()
                                .user(userSpec -> userSpec
                                        .text(nameOfTheGameTemplateResource)
                                        .param("document", firstDocument.getText()))
                                .call()
                                .entity(GameTitle.class);

                        if (Objects.requireNonNull(gameTitle).title().equals("UNKNOWN")) {
                            LOGGER.warn("Unable to determine the name of a game; " +
                                    "not adding to vector store.");
                            documents = Collections.emptyList();
                            return documents;
                        }

                        LOGGER.info("Determined game title to be {}", gameTitle.title());
                        documents = documents.stream().peek(document -> {
                            document.getMetadata()
                                    .put("gameTitle", gameTitle.getNormalizedTitle());
                        }).toList();
                    }

                    return documents;
                });
    }


}
