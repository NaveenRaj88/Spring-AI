package org.spring.ai.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spring.ai.model.Question;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SpringAiBoardGameServiceTests {

    @Autowired
    private BoardGameService boardGameService;

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    private RelevancyEvaluator relevancyEvaluator;

    private FactCheckingEvaluator factCheckingEvaluator;

    @BeforeEach

    public void setup() {
        this.relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);
        this.factCheckingEvaluator = FactCheckingEvaluator.builder(chatClientBuilder).build();
    }

//    @Test
    public void evaluateRelevancy(){
        var userText = "why is the sky blue?";
        Question question = new Question("",userText);
        var answer = boardGameService.askQuestion(question);

        var request = new EvaluationRequest(userText, answer.answer());
        var evaluationResponse = relevancyEvaluator.evaluate(request);

        Assertions.assertThat(evaluationResponse.isPass())
                .withFailMessage("""
          ========================================
          The answer "%s"
          is not considered relevant to the question
          "%s".
          ========================================
          """, answer.answer(), userText)
                .isTrue();

        System.out.println("Relevancy score: " + evaluationResponse);
    }

//    @Test
    public void evaluateFactChecking(){
        var userText = "what is the capital of France?";
        var question = new Question("",userText);
        var answer = boardGameService.askQuestion(question);

        var request = new EvaluationRequest(userText, answer.answer());
        var evaluationResponse = factCheckingEvaluator.evaluate(request);

        Assertions.assertThat(evaluationResponse.isPass())
                .withFailMessage("""
          ========================================
          The answer "%s"
          is not considered factually correct for the question
          "%s".
          ========================================
          """, answer.answer(), userText)
                .isTrue();

        System.out.println("Fact-checking score: " + evaluationResponse);
    }
}
