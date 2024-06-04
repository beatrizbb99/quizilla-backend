package de.hsrm.quiz_gateway.services.question;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

import de.hsrm.quiz_gateway.entities.Question;
import de.hsrm.quiz_gateway.entities.Quiz;
import de.hsrm.quiz_gateway.enums.CollectionName;

@Service
public class QuestionService {

    private static final String COLLECTION_NAME = CollectionName.QUESTIONS.getName();

    public String createQuestion(Question question) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        // Check if the category exists
        String cat_id = question.getCategory_id();
        CollectionReference categoriesRef = dbFirestore.collection(CollectionName.CATEGORIES.getName());
        Query query = categoriesRef.whereEqualTo("category_id", cat_id);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (documents.isEmpty()) {
            throw new IllegalArgumentException("Category does not exist: " + cat_id);
        }

        // Get the collection reference
        CollectionReference collectionRef = dbFirestore.collection(COLLECTION_NAME);

        // Generate "locally" a new document for the given collection reference
        DocumentReference docRef = collectionRef.document();

        // Get the new document Id
        String documentUuid = docRef.getId();

        question.setQuestion_id(documentUuid);

        ApiFuture<WriteResult> writeResult = docRef.set(question);
        writeResult.get();

        return documentUuid;

    }

    public Question getQuestion(String question_id) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
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
        Firestore dbFirestore = FirestoreClient.getFirestore();
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
        Firestore dbFirestore = FirestoreClient.getFirestore();
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
        Firestore dbFirestore = FirestoreClient.getFirestore();
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
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = dbFirestore.collection(COLLECTION_NAME).document(question_id).delete();

        // Suche nach Quizzen, die die gelöschte Kategorie referenzieren
        Query query = dbFirestore.collection(CollectionName.QUIZZES.getName()).whereArrayContains("question_ids",
                question_id);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            Quiz quiz = document.toObject(Quiz.class);
            List<String> questionIds = quiz.getQuestion_ids();
            questionIds.remove(question_id);
            document.getReference().update("question_ids", questionIds);
        }

        return "Successfully deleted question " + question_id;
    }
}
