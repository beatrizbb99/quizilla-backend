package de.hsrm.quiz_gateway.entities;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Answer {
    private String answer_id;
    private String question_id;
    private String answer;
}
