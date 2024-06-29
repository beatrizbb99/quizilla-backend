package de.hsrm.quiz_gateway.firebase.firestore.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.hsrm.quiz_gateway.entities.Quiz;
import de.hsrm.quiz_gateway.firebase.firestore.services.UserService;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("api/users")
public class UsersController {
    
    public UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{user_id}/quizzes")
    public List<Quiz> getUserQuizzes(@PathVariable("user_id") String user_id) throws InterruptedException, ExecutionException {
        return userService.getUserQuizzes(user_id);
    }
    
}
