package de.hsrm.quiz_gateway.firebase.firestore.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;

import de.hsrm.quiz_gateway.entities.Quiz;
import de.hsrm.quiz_gateway.entities.User;
import de.hsrm.quiz_gateway.firebase.firestore.enums.CollectionName;

@Service
public class UserService {

    @Autowired
    private Firestore dbFirestore;

    private static final String COLLECTION_NAME = CollectionName.USERS.getName();

    public String createUser(String username, int userId) throws InterruptedException, ExecutionException {
        String user_id = String.valueOf(userId);
        CollectionReference collectionRef = dbFirestore.collection(COLLECTION_NAME);
        DocumentReference docRef = collectionRef.document(user_id);
        User user = new User();
        user.setName(username);
        user.setUser_id(user_id);

        ApiFuture<WriteResult> writeResult = docRef.set(user);
        writeResult.get();

        return "User mit id: " + user_id + "in firebase hinzugefügt.";
    }

    public List<Quiz> getUserQuizzes(String user_id) throws InterruptedException, ExecutionException {
        DocumentReference documentReference = dbFirestore.collection(COLLECTION_NAME).document(user_id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();

        List<Quiz> quizzes = new ArrayList<>();

        if (document.exists() && document.contains("quiz_ids")) {
            User user = document.toObject(User.class);
            List<String> quizIds = user.getQuiz_ids();

            for (String quizId : quizIds) {
                DocumentReference quizRef = dbFirestore.collection(CollectionName.QUIZZES.getName()).document(quizId);
                ApiFuture<DocumentSnapshot> quizFuture = quizRef.get();
                DocumentSnapshot quizDocument = quizFuture.get();

                if (quizDocument.exists()) {
                    Quiz quiz = quizDocument.toObject(Quiz.class);
                    quizzes.add(quiz);
                }
            }
        }

        return quizzes;
    }
}
