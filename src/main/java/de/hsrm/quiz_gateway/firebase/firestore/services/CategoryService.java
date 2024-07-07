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

import de.hsrm.quiz_gateway.entities.Category;
import de.hsrm.quiz_gateway.firebase.firestore.enums.CollectionName;

@Service
public class CategoryService {

    @Autowired
    private Firestore dbFirestore;

    private static final String COLLECTION_NAME = CollectionName.CATEGORIES.getName();

    public String createCategory(Category category) throws InterruptedException, ExecutionException {

        /*
         * // Generiere eine UUID für die Kategorie
         * String categoryId = UUID.randomUUID().toString();
         * 
         * // Setze die UUID als ID für die Kategorie
         * category.setCategory_id(categoryId);
         * 
         * // Speichere die Kategorie in Firestore mit dem Kategorie-Namen als
         * Dokument-ID
         * ApiFuture<WriteResult> collectionsApiFuture =
         * dbFirestore.collection(COLLECTION_NAME).document(category.getName()).set(
         * category);
         * 
         * // Rückgabe der Aktualisierungszeit des Dokuments
         * return collectionsApiFuture.get().getUpdateTime().toString();
         */

        // Überprüfen, ob eine Kategorie mit dem gleichen Namen bereits vorhanden ist
        Query query = dbFirestore.collection(COLLECTION_NAME).whereEqualTo("name", category.getName());
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (!documents.isEmpty()) {
            return "Category with name " + category.getName() + " already exists";
        }

        CollectionReference collectionRef = dbFirestore.collection(COLLECTION_NAME);
        DocumentReference docRef = collectionRef.document();
        String documentUuid = docRef.getId();
        category.setCategory_id(documentUuid);
        ApiFuture<WriteResult> writeResult = docRef.set(category);
        writeResult.get();

        return documentUuid;
    }

    public Category getCategory(String category_id) throws InterruptedException, ExecutionException {
        DocumentReference documentReference = dbFirestore.collection(COLLECTION_NAME).document(category_id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        Category category;
        if (document.exists()) {
            category = document.toObject(Category.class);
            return category;
        }

        return null;
    }

    public List<Category> getAllCategories() throws InterruptedException, ExecutionException {
        CollectionReference categoriesRef = dbFirestore.collection(COLLECTION_NAME);
        List<Category> categories = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = categoriesRef.get();
        QuerySnapshot querySnapshot = future.get();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            Category category = document.toObject(Category.class);
            categories.add(category);
        }

        return categories;
    }

    public String updateCategory(String category_id, Category category) throws InterruptedException, ExecutionException {
        DocumentReference catRef = dbFirestore.collection(COLLECTION_NAME).document(category_id);

        ApiFuture<DocumentSnapshot> future = catRef.get();
        DocumentSnapshot document = future.get();
        if (!document.exists()) {
            throw new IllegalArgumentException("Quiz with id " + category_id + " does not exist");
        }

        ApiFuture<WriteResult> updateFuture = catRef.set(category);
        updateFuture.get();
        return "Successfully updated category with id " + category_id;
    }

    public String deleteCategory(String category_id, String cat_name) throws InterruptedException, ExecutionException {
        ApiFuture<WriteResult> writeResult = dbFirestore.collection(COLLECTION_NAME).document(category_id).delete();

        // Suche nach Fragen, die die gelöschte Kategorie referenzieren
        Query query = dbFirestore.collection(CollectionName.QUESTIONS.getName()).whereEqualTo("category", cat_name);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            document.getReference().update("category", "Allgemein");
        }

        // Suche nach Quizzen, die die gelöschte Kategorie referenzieren
        Query queryQuiz = dbFirestore.collection(CollectionName.QUIZZES.getName()).whereEqualTo("category", cat_name);
        ApiFuture<QuerySnapshot> querySnapshotQuiz = queryQuiz.get();
        for (DocumentSnapshot document : querySnapshotQuiz.get().getDocuments()) {
            document.getReference().update("category", "Allgemein");
        }
        
        return "Successfully deleted category " + cat_name;
    }

}
