package de.hsrm.quiz_gateway.firebase.firestore.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import de.hsrm.quiz_gateway.entities.Question;
import de.hsrm.quiz_gateway.entities.Quiz;
import de.hsrm.quiz_gateway.firebase.firestore.enums.CollectionName;

@Service
public class QuizService {

    private static final String COLLECTION_NAME = CollectionName.QUIZZES.getName();

    public String createQuiz(Quiz quiz) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        
        CollectionReference collectionRef = dbFirestore.collection(COLLECTION_NAME);
        DocumentReference docRef = collectionRef.document();
        String documentUuid = docRef.getId();
        quiz.setQuiz_id(documentUuid);
        int totalPoints = calculateTotalPoints(quiz.getQuestion_ids());
        quiz.setPoints(totalPoints);

        ApiFuture<WriteResult> writeResult = docRef.set(quiz);
        writeResult.get();

        return documentUuid;
    }

    public String addQuestionToQuiz(String quiz_id, String question_id)
            throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        // Check if the question exists
        DocumentReference questionRef = dbFirestore.collection(CollectionName.QUESTIONS.getName())
                .document(question_id);
        ApiFuture<DocumentSnapshot> questionFuture = questionRef.get();
        DocumentSnapshot questionDoc = questionFuture.get();

        if (!questionDoc.exists()) {
            throw new IllegalArgumentException("Question with id: " + question_id + " does not exist");
        }

        // Check if the quiz exists
        DocumentReference quizRef = dbFirestore.collection(COLLECTION_NAME).document(quiz_id);
        ApiFuture<DocumentSnapshot> quizFuture = quizRef.get();
        DocumentSnapshot quizDoc = quizFuture.get();

        if (quizDoc.exists()) {
            Quiz quiz = quizDoc.toObject(Quiz.class);
            List<String> questionIds = quiz.getQuestion_ids();
            if (!questionIds.contains(question_id)) {
                Question question = questionDoc.toObject(Question.class);
                int points = quiz.getPoints();
                points += question.getPoints();
                quiz.setPoints(points);
                questionIds.add(question_id);
                quiz.setQuestion_ids(questionIds);

                // Update the quiz
                ApiFuture<WriteResult> updateFuture = quizRef.set(quiz);
                updateFuture.get();
                return "Question added to quiz with id: " + quiz_id + " with question id: " + question_id;
            } else {
                return "Question with id: " + question_id + " already exists in quiz with id: " + quiz_id;
            }
        } else {
            return "Quiz with id: " + quiz_id + " does not exist";
        }
    }

    public Quiz getQuiz(String quiz_id) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection(COLLECTION_NAME).document(quiz_id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        Quiz quiz;
        if (document.exists()) {
            quiz = document.toObject(Quiz.class);
            return quiz;
        }

        return null;
    }

    public List<Quiz> getAllQuizzes() throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        CollectionReference quizRef = dbFirestore.collection(COLLECTION_NAME);

        List<Quiz> quizzes = new ArrayList<>();

        ApiFuture<QuerySnapshot> future = quizRef.get();
        QuerySnapshot querySnapshot = future.get();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            Quiz quiz = document.toObject(Quiz.class);
            quizzes.add(quiz);
        }

        return quizzes;
    }

    public String updateQuiz(String quiz_id, Quiz quiz) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference quizRef = dbFirestore.collection(COLLECTION_NAME).document(quiz_id);

        ApiFuture<DocumentSnapshot> future = quizRef.get();
        DocumentSnapshot document = future.get();
        if (!document.exists()) {
            throw new IllegalArgumentException("Quiz with id " + quiz_id + " does not exist");
        }

        int totalPoints = calculateTotalPoints(quiz.getQuestion_ids());
        quiz.setPoints(totalPoints);

        ApiFuture<WriteResult> updateFuture = quizRef.set(quiz);
        updateFuture.get();
        return "Successfully updated quiz with id " + quiz_id;
    }

    public String deleteQuestionFromQuiz(String quiz_id, String question_id)
            throws InterruptedException, ExecutionException {

        Firestore dbFirestore = FirestoreClient.getFirestore();

        DocumentReference quizRef = dbFirestore.collection(COLLECTION_NAME).document(quiz_id);
        ApiFuture<DocumentSnapshot> quizFuture = quizRef.get();
        DocumentSnapshot quizDoc = quizFuture.get();
        if (!quizDoc.exists()) {
            throw new IllegalArgumentException("Quiz with id " + quiz_id + " does not exist");
        }

        Quiz quiz = quizDoc.toObject(Quiz.class);
        List<String> questionIds = quiz.getQuestion_ids();
        if (!questionIds.contains(question_id)) {
            throw new IllegalArgumentException(
                    "Question with id " + question_id + " does not exist in quiz with ID " + quiz_id);
        }

        DocumentReference questionRef = dbFirestore.collection(CollectionName.QUESTIONS.getName()).document(question_id);
        ApiFuture<DocumentSnapshot> questionFuture = questionRef.get();
        DocumentSnapshot questionDoc = questionFuture.get();
        Question question = questionDoc.toObject(Question.class);
        int points = quiz.getPoints();
        points -= question.getPoints();

        quiz.setPoints(points);
        questionIds.remove(question_id);
        quiz.setQuestion_ids(questionIds);

        ApiFuture<WriteResult> updateFuture = quizRef.set(quiz);
        updateFuture.get();

        return "Successfully deleted question with id " + question_id + "from quiz with id " + quiz_id;
    }

    public String deleteQuiz(String quiz_id) {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = dbFirestore.collection(COLLECTION_NAME).document(quiz_id).delete();
        return "Successfully deleted quiz with id " + quiz_id;
    }

    private int calculateTotalPoints(List<String> questionIds) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        int totalPoints = 0;
        for (String questionId : questionIds) {
            DocumentReference questionRef = dbFirestore.collection(CollectionName.QUESTIONS.getName()).document(questionId);
            ApiFuture<DocumentSnapshot> future = questionRef.get();
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                int points = document.getLong("points").intValue();
                totalPoints += points;
            }
        }
        return totalPoints;
    }


}
