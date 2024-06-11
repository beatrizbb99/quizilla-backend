package de.hsrm.quiz_gateway.entities;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Quiz {
    private String quiz_id;
    private String category;
    private String name;
    private int points = 0;
    private int time;
    private List<String> question_ids = new ArrayList<>();;
}
