package org.spring.ai.service;

import org.spring.ai.model.Answer;
import org.spring.ai.model.Question;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class SpringAiBoardGameService implements BoardGameService {


    private ChatClient chatClient;
    public SpringAiBoardGameService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }
    @Override
    public Answer askQuestion(Question question) {
        var answerText = chatClient.prompt().user(question.question()).call().content();
        return new Answer(answerText);
    }


}
