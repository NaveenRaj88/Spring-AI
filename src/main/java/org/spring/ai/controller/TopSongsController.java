package org.spring.ai.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import tools.jackson.databind.JsonNode;

import java.util.List;

@RestController
public class TopSongsController {

    private static final Logger logger = LoggerFactory.getLogger(TopSongsController.class);
    @Value("classpath:/templates/top-songs-prompt.st")
    private Resource topSongPromptTemplate;

    private ChatClient chatClient;

    public TopSongsController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping(path = "/topSongs", produces = "application/json")
    public List<String> topSongs(@RequestParam("year") String year) {
        ChatOptions.Builder<?> chatOptions = ChatOptions.builder().model("gemma3:4b");
        return chatClient.prompt().user(userSpec -> userSpec.text(topSongPromptTemplate).param("year", year))
                .options(chatOptions)
                .call().entity(new ParameterizedTypeReference<List<String>>() {
                });
    }

    @GetMapping(path = "/topSongsStream", produces = "application/ndjson")
    public Flux<String> topSongsStreaming(@RequestParam("year") String year) {
        ChatOptions.Builder<?> chatOptions = ChatOptions.builder().model("gemma3:4b");
        return chatClient.prompt().user(userSpec -> userSpec.text(topSongPromptTemplate).param("year", year))
                .options(chatOptions)
                .stream().content();
    }

    @GetMapping(path = "/topSongsresponseMetadata", produces = "application/json")
    public List<String> topSongsWithResponseMetadata(@RequestParam("year") String year) {
        ChatOptions.Builder<?> chatOptions = ChatOptions.builder().model("gemma3:4b");
        var responseEntity = chatClient.prompt().user(userSpec -> userSpec.text(topSongPromptTemplate).param("year", year))
                .options(chatOptions)
                .call().responseEntity(new ParameterizedTypeReference<List<String>>() {
                });

        var response = responseEntity.response();
        var metadata = response.getMetadata();
        logUsage(metadata.getUsage());

        return responseEntity.entity();
    }

    @PostMapping(path = "/genericQuestion", produces = "application/json")
    public JsonNode genericQuestion(@RequestBody String question) {
        ChatOptions.Builder<?> chatOptions = ChatOptions.builder().model("gemma3:4b");
        var responseEntity = chatClient.prompt().user(userSpec -> userSpec.text(question))
                .options(chatOptions)
                .call().responseEntity(JsonNode.class);

        var response = responseEntity.response();
        var metadata = response.getMetadata();
        logUsage(metadata.getUsage());

        return responseEntity.entity();
    }

    private void logUsage(Usage usage) {
        logger.info("Token usage: prompt={}, generation={}, total={}",
                usage.getPromptTokens(),
                usage.getCompletionTokens(),
                usage.getTotalTokens());
    }

}
