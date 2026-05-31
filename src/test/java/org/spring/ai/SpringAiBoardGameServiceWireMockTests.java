package org.spring.ai;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spring.ai.model.Question;
import org.spring.ai.service.SpringAiBoardGameService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.Charset;

@EnableWireMock(@ConfigureWireMock(baseUrlProperties = "openai.base.url"))
@SpringBootTest(properties = "spring.ai.openai.base-url=${openai.base.url}")
public class SpringAiBoardGameServiceWireMockTests {

    @Value("classpath:/test-openai-response.json")
    Resource responseResource;

    @Autowired
    ChatClient.Builder chatClientBuilder;


    @BeforeEach
    public void setup() throws IOException {
        var cannedResponse = responseResource.getContentAsString(Charset.defaultCharset());
        var mapper = new ObjectMapper() ;
        var response = mapper.readTree(cannedResponse);
        WireMock.stubFor(WireMock.post("/v1/chat/completions")
                .willReturn(ResponseDefinitionBuilder.like(ResponseDefinition.okForJson(response))));

    }

    @Test
    public void testAskQuestion() {
        var boardGameService =
                new SpringAiBoardGameService(chatClientBuilder);
        var answer =
                boardGameService.askQuestion(
                        new Question("What is the capital of France?"));
        Assertions.assertThat(answer).isNotNull();
        Assertions.assertThat(answer.answer()).isEqualTo("Paris");
    }

}
