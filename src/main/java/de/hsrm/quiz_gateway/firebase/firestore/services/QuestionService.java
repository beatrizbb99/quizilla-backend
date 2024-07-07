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
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import de.hsrm.quiz_gateway.entities.Answer;
import de.hsrm.quiz_gateway.entities.Question;
import de.hsrm.quiz_gateway.entities.Quiz;
import de.hsrm.quiz_gateway.firebase.firestore.enums.CollectionName;

@Service
public class QuestionService {

    @Autowired
    private Firestore dbFirestore;

    private static final String COLLECTION_NAME = CollectionName.QUESTIONS.getName();

    public String createQuestion(Question question) throws InterruptedException, ExecutionException {
        CollectionReference collectionRef = dbFirestore.collection(COLLECTION_NAME);
        DocumentReference docRef = collectionRef.document();
        String documentUuid = docRef.getId();
        question.setQuestion_id(documentUuid);
        ApiFuture<WriteResult> writeResult = docRef.set(question);
        writeResult.get();

        return documentUuid;
    }

    public Question getQuestion(String question_id) throws InterruptedException, ExecutionException {
        DocumentReference documentReference = dbFirestore.collection(COLLECTION_NAME).document(question_id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        Question question;
        if (document.exists()) {
            question = document.toObject(Question.class);
            return question;
        }

        return null;
    }

    public List<Question> getAllQuestions() throws InterruptedException, ExecutionException {
        CollectionReference collectionRef = dbFirestore.collection(COLLECTION_NAME);
        List<Question> questions = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = collectionRef.get();
        QuerySnapshot querySnapshot = future.get();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            Question question = document.toObject(Question.class);
            questions.add(question);
        }

        return questions;
    }

    public List<Question> getAllQuestionsWithIds(List<String> questionIds)
            throws InterruptedException, ExecutionException {
        CollectionReference collectionRef = dbFirestore.collection(COLLECTION_NAME);
        List<Question> questions = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = collectionRef.whereIn("question_id", questionIds).get();
        QuerySnapshot querySnapshot = future.get();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            Question question = document.toObject(Question.class);
            questions.add(question);
        }

        return questions;
    }

    public String updateQuestion(String question_id, Question question)
            throws InterruptedException, ExecutionException {
        DocumentReference questionRef = dbFirestore.collection(COLLECTION_NAME).document(question_id);

        ApiFuture<DocumentSnapshot> future = questionRef.get();
        DocumentSnapshot document = future.get();
        if (!document.exists()) {
            throw new IllegalArgumentException("Question with id " + question_id + " does not exist");
        }

        ApiFuture<WriteResult> updateFuture = questionRef.set(question);
        updateFuture.get();
        return "Successfully updated question with id " + question_id;
    }

    public String deleteQuestion(String question_id) throws InterruptedException, ExecutionException {
        ApiFuture<WriteResult> writeResult = dbFirestore.collection(COLLECTION_NAME).document(question_id).delete();

        // Suche nach Quizzen, die die gelöschte Frage referenzieren
        Query quizQuery = dbFirestore.collection(CollectionName.QUIZZES.getName()).whereArrayContains("question_ids",
                question_id);
        ApiFuture<QuerySnapshot> quizQuerySnapshot = quizQuery.get();
        for (DocumentSnapshot document : quizQuerySnapshot.get().getDocuments()) {
            Quiz quiz = document.toObject(Quiz.class);
            List<String> questionIds = quiz.getQuestion_ids();
            questionIds.remove(question_id);
            document.getReference().update("question_ids", questionIds);
        }

        // Suche nach Antworten, die die gelöschte Frage referenzieren
        Query answerQuery = dbFirestore.collection(CollectionName.ANSWERS.getName()).whereEqualTo("question_id",
                question_id);
        ApiFuture<QuerySnapshot> answerQuerySnapshot = answerQuery.get();
        for (DocumentSnapshot document : answerQuerySnapshot.get().getDocuments()) {
            document.getReference().delete();
        }

        return "Successfully deleted question " + question_id;
    }
}
