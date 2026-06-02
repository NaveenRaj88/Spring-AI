package org.spring.ai.service;

import org.spring.ai.model.Answer;
import org.spring.ai.model.Question;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service("SpringAiBoardGameEntityService")
public class SpringAiBoardGameEntityService implements BoardGameService {

    @Value("classpath:/templates/questionPromptTemplate.st")
    Resource questionPromptTemplate;

    @Value("classpath:/templates/systemPromptTemplate.st")
    Resource promptTemplate;

    private ChatClient chatClient;
    private GameRulesService gameRulesService;

    public SpringAiBoardGameEntityService(ChatClient.Builder chatClientBuilder, GameRulesService gameRulesService) {
        this.chatClient = chatClientBuilder.build();
        this.gameRulesService = gameRulesService;
    }

    @Override
    public Answer askQuestion(Question question) {


        // using the prompt template for system message and user message
        var gameRules = gameRulesService.getRulesFor(question.gameTitle());

        ChatOptions.Builder<?> chatOptions= ChatOptions.builder().model("llama3.2:3b");
        var answerText = chatClient.prompt().system(promptSystemSpec -> promptSystemSpec.text(promptTemplate)
                        .param("gameTitle", question.gameTitle()).param("rules", gameRules))
                .user(promptUserSpec -> promptUserSpec.text(question.question()))
                .options(chatOptions)
                .call().entity(Answer.class);

        return answerText;

    }



}