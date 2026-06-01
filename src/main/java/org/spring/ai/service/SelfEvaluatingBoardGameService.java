package org.spring.ai.service;

import org.spring.ai.exception.AnswerNotRelevantException;
import org.spring.ai.model.Answer;
import org.spring.ai.model.Question;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.retry.annotation.Recover;

public class SelfEvaluatingBoardGameService implements BoardGameService {

    private final ChatClient chatClient;
    private final RelevancyEvaluator evaluator;

    public SelfEvaluatingBoardGameService(ChatClient.Builder chatClientBuilder) {
        var chatOptionsBuilder = ChatOptions.builder()
                .model("llama3.2:3b");


        this.chatClient = chatClientBuilder
                .defaultOptions(chatOptionsBuilder)
                .build();

        this.evaluator = new RelevancyEvaluator(chatClientBuilder);
    }

    @Recover
    public Answer recover(AnswerNotRelevantException e) {
        return new Answer("","I'm sorry, I wasn't able to answer the question.");
    }

    private void evaluateRelevancy(Question question, String answerText) {
        var evaluationRequest =
                new EvaluationRequest(question.question(), answerText);
        var evaluationResponse = evaluator.evaluate(evaluationRequest);
        if (!evaluationResponse.isPass()) {
            throw new AnswerNotRelevantException(question.question(), answerText);
        }
    }

    @Override
    public Answer askQuestion(Question question) {
        return null;
    }
}
