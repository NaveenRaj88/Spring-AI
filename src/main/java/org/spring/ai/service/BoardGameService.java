package org.spring.ai.service;

import org.spring.ai.model.Answer;
import org.spring.ai.model.Question;

public interface BoardGameService {
    Answer askQuestion(Question question);
}
