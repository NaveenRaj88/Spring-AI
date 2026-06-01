package org.spring.ai.service;

import org.spring.ai.model.Answer;
import org.spring.ai.model.Question;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class SpringAiBoardGameService implements BoardGameService {

    @Value("classpath:/templates/questionPromptTemplate.st")
    Resource questionPromptTemplate;

    private ChatClient chatClient;
    public SpringAiBoardGameService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }
    @Override
    public Answer askQuestion(Question question) {
//        var promtTemplate = """
//                 You are a helpful assistant, answering questions about tabletop games.
//                    If you don't know anything about the game or don't know the answer,
//                    say "I don't know".
//                 Answer this question about {game}: {question}
//                 """;


//        var prompt = "Answer this question about " + question.gameTitle() + ": " + question.question();
//        var answerText = chatClient.prompt().user(prompt).call().content();

        var answerText = chatClient.prompt().user(promptUserSpec ->  promptUserSpec.text(questionPromptTemplate)
                .param("gameTitle", question.gameTitle()).param("question", question.question())).call().content();

        return new Answer(question.gameTitle(), answerText);

    }


}
