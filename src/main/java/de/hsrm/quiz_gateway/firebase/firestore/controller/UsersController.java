package de.hsrm.quiz_gateway.firebase.firestore.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.hsrm.quiz_gateway.entities.Quiz;
import de.hsrm.quiz_gateway.firebase.firestore.services.UserService;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("api/users")
@CrossOrigin(origins = "http://34.95.109.147")
public class UsersController {
    
    public UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{user_id}/quizzes")
    public List<Quiz> getUserQuizzes(@PathVariable String user_id) throws InterruptedException, ExecutionException {
        return userService.getUserQuizzes(user_id);
    }
    
}
