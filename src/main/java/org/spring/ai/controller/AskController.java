package org.spring.ai.controller;

import jakarta.validation.Valid;
import org.spring.ai.model.Answer;
import org.spring.ai.model.Question;
import org.spring.ai.service.BoardGameService;
import org.spring.ai.service.SpringAiBoardGameEntityService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AskController {

    private BoardGameService boardGameService;

        public AskController(@Qualifier("SpringAiBoardGameEntityService") BoardGameService boardGameService) {
        this.boardGameService = boardGameService;
    }


    @PostMapping(path = "/ask", produces = "application/json")
    public Answer askQuestion(@RequestBody @Valid  Question question) {
        var answer = boardGameService.askQuestion(question);
        return new Answer("",answer.answer());
    }

    @PostMapping(path = "/askEntity", produces = "application/json")
    public Answer askQuestionEntity(@RequestBody @Valid  Question question) {
        var answer = boardGameService.askQuestion(question);
        return new Answer("",answer.answer());
    }
}
