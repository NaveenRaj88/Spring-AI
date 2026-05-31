package org.spring.ai.controller;

import org.spring.ai.model.Answer;
import org.spring.ai.model.Question;
import org.spring.ai.service.BoardGameService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AskController {

    private BoardGameService boardGameService;

        public AskController(BoardGameService boardGameService) {
        this.boardGameService = boardGameService;
    }


    @PostMapping(path = "/ask", produces = "application/json")
    public Answer askQuestion(String question) {
        var answer = boardGameService.askQuestion(new Question(question));
        return new Answer(answer.answer());
    }

}
