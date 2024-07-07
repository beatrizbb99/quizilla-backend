package de.hsrm.quiz_gateway.firebase.firestore.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.hsrm.quiz_gateway.entities.Quiz;
import de.hsrm.quiz_gateway.firebase.firestore.services.QuizService;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("api/quizzes")
public class QuizController {

    public QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/create/{user_id}")
    public String createQuiz(@RequestBody Quiz quiz, @PathVariable String user_id) throws InterruptedException, ExecutionException {
        return quizService.createQuiz(quiz, user_id);
    }

    @PutMapping("quiz/{quiz_id}/question/{question_id}")
    public String addQuestionToQuiz(@PathVariable String quiz_id, @PathVariable String question_id) throws InterruptedException, ExecutionException {
        return quizService.addQuestionToQuiz(quiz_id, question_id);
    }
    

    @GetMapping("/{quiz_id}")
    public Quiz getQuiz(@PathVariable String quiz_id) throws InterruptedException, ExecutionException {
        return quizService.getQuiz(quiz_id);
    }

    @GetMapping
    public List<Quiz> getAllQuizzes() throws InterruptedException, ExecutionException {
        return quizService.getAllQuizzes();
    }

    @PutMapping("/update/{quiz_id}")
    public String updateQuiz(@PathVariable String quiz_id, @RequestBody Quiz quiz) throws InterruptedException, ExecutionException {
       return quizService.updateQuiz(quiz_id, quiz);
    }

    @PutMapping("/delete/quiz/{quiz_id}/question/{question_id}")
    public String deleteQuestionFromQuiz(@PathVariable String quiz_id, @PathVariable String question_id) throws InterruptedException, ExecutionException {
        return quizService.deleteQuestionFromQuiz(quiz_id, question_id);
    }

    @DeleteMapping("/delete/{quiz_id}/user/{user_id}")
    public String deleteQuiz(@PathVariable String quiz_id, @PathVariable String user_id) throws InterruptedException, ExecutionException {
        return quizService.deleteQuiz(quiz_id, user_id);
    }
    

    
}
