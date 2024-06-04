package de.hsrm.quiz_gateway.entities;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Category {
    private String category_id;
    private String name;
    private String description;
}
