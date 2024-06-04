package de.hsrm.quiz_gateway.entities;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Question {
    private String question_id;
    private String category_id;
    private List<String> options;
    private int points;
    private int answer;
    private String question;
}
