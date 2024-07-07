package de.hsrm.quiz_gateway.firebase.firestore.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import de.hsrm.quiz_gateway.entities.Answer;
import de.hsrm.quiz_gateway.entities.Question;
import de.hsrm.quiz_gateway.firebase.firestore.enums.CollectionName;

@Service
public class AnswerService {

    @Autowired
    private Firestore dbFirestore;

    private static final String COLLECTION_NAME = CollectionName.ANSWERS.getName();

    public String createAnswer(Answer answer, String question_id) throws InterruptedException, ExecutionException {
        CollectionReference collectionRef = dbFirestore.collection(COLLECTION_NAME);
        DocumentReference docRef = collectionRef.document();
        String documentUuid = docRef.getId();
        answer.setAnswer_id(documentUuid);
        answer.setQuestion_id(question_id);
        ApiFuture<WriteResult> writeResult = docRef.set(answer);
        writeResult.get();

        return documentUuid;
    }

    public String getAnswer(String question_id) throws InterruptedException, ExecutionException {
        Query query  = dbFirestore.collection(COLLECTION_NAME).whereEqualTo("question_id", question_id);
        ApiFuture<QuerySnapshot> future = query.get();
        QuerySnapshot querySnapshot = future.get();
        if (!querySnapshot.isEmpty()) {
            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
            Answer answer = document.toObject(Answer.class);
            return answer.getAnswer();
        } else {
            return null;
        }
    }

    public Map<String, Object> checkAnswers(Map<String, String> answers)
            throws InterruptedException, ExecutionException {
        CollectionReference collectionReference = dbFirestore.collection(COLLECTION_NAME);
        Map<String, Object> result = new HashMap<>();

        int totalScore = 0;
        int totalPoints = 0;

        for (Map.Entry<String, String> entry : answers.entrySet()) {
            String question_id = entry.getKey();
            String userAnswer = entry.getValue();

            Query query = collectionReference.whereEqualTo("question_id", question_id);
            ApiFuture<QuerySnapshot> querySnapshot = query.get();

            List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
            if (!documents.isEmpty()) {
                DocumentSnapshot document = documents.get(0);
                Answer answer = document.toObject(Answer.class);
                String correctAnswer = answer.getAnswer();

                int points = 0;
                String questionId = answer.getQuestion_id();
                DocumentSnapshot questionDoc = dbFirestore.collection(CollectionName.QUESTIONS.getName()).document(questionId).get().get();
                if (questionDoc.exists()) {
                    Question question = questionDoc.toObject(Question.class);
                    points = question.getPoints();
                }

                Map<String, Object> answerResult = new HashMap<>();
                boolean isCorrect = userAnswer.equals(correctAnswer);
                answerResult.put("selectedAnswer", userAnswer);
                answerResult.put("correctAnswer", correctAnswer);
                answerResult.put("isCorrect", isCorrect);

                if (isCorrect) {
                    totalScore++;
                    totalPoints += points;
                }

                result.put(question_id, answerResult);
            } else {
                // question not found
            }
        }

        result.put("score", totalScore);
        result.put("points", totalPoints);

        return result;
    }

}
