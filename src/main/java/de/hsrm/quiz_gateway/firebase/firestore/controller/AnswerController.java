package de.hsrm.quiz_gateway.firebase.firestore.controller;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.hsrm.quiz_gateway.entities.Answer;
import de.hsrm.quiz_gateway.entities.Category;
import de.hsrm.quiz_gateway.firebase.firestore.services.AnswerService;

@RestController
@RequestMapping("api/answers")
public class AnswerController {

    public AnswerService answerService;

    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping("/create/{question_id}")
    public String createAnswer(@RequestBody Answer answer, @PathVariable("question_id") String question_id) throws InterruptedException, ExecutionException {
        return answerService.createAnswer(answer, question_id);
    }

    @GetMapping("/{question_id}")
    public String getAnswer(@PathVariable("question_id") String question_id) throws InterruptedException, ExecutionException {
        return answerService.getAnswer(question_id);
    }

    @PostMapping("/submit")
    public Map<String, Object> checkAnswers(@RequestBody Map<String, String> answers) throws InterruptedException, ExecutionException {
        return answerService.checkAnswers(answers);
    }

    





}
