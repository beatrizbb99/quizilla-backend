package de.hsrm.quiz_gateway.firebase.firestore.enums;

public enum CollectionName {
    QUIZZES("Quizzes"),
    QUESTIONS("Questions"),
    CATEGORIES("Categories");

    private final String name;

    CollectionName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}