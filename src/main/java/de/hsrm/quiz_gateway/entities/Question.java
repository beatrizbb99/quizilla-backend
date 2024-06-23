package de.hsrm.quiz_gateway.entities;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Question {
    private String question_id;
    private String category;
    private List<String> options;
    private int points;
    private String question;
    private String mediaPath;

}
