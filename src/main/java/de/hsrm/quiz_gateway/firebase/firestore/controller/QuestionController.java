package de.hsrm.quiz_gateway.firebase.firestore.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.hsrm.quiz_gateway.entities.Question;
import de.hsrm.quiz_gateway.firebase.firestore.services.QuestionService;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("api/questions")
@CrossOrigin(origins = "http://34.149.22.243")
public class QuestionController {

    public QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/create")
    public String createQuestion(@RequestBody Question question) throws InterruptedException, ExecutionException {
        return questionService.createQuestion(question);
    }

    @GetMapping("/{question_id}")
    public Question getQuestion(@PathVariable("question_id") String question_id) throws InterruptedException, ExecutionException {
        return questionService.getQuestion(question_id);
    }

    @GetMapping
    public List<Question> getAllQuestions() throws InterruptedException, ExecutionException {
        return questionService.getAllQuestions();
    }

    @PostMapping("/getByIds")
    public List<Question> getAllQuestionsWithIds(@RequestBody List<String> questionIds) throws InterruptedException, ExecutionException {
        return questionService.getAllQuestionsWithIds(questionIds);
    }

    @PutMapping("/update/{question_id}")
    public String updateQuestion(@PathVariable("question_id") String question_id, @RequestBody Question question) throws InterruptedException, ExecutionException {
        return questionService.updateQuestion(question_id, question);
    }

    @DeleteMapping("/delete/{question_id}")
    public String deleteQuestion(@PathVariable("question_id") String question_id) throws InterruptedException, ExecutionException {
        return questionService.deleteQuestion(question_id);
    }
    

    
}
