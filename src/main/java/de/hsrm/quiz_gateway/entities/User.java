package de.hsrm.quiz_gateway.entities;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    private String name;
    private String user_id;
    private List<String> quiz_ids = new ArrayList<>(); 
}
