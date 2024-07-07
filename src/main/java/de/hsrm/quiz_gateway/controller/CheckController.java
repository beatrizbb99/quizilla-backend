package de.hsrm.quiz_gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckController {

    @GetMapping("/readiness")
    public ResponseEntity<String> readinessCheck() {
        // Hier können Sie die Logik für den Readiness-Check implementieren
        // Zum Beispiel können Sie überprüfen, ob wichtige Ressourcen geladen sind
        // oder andere kritische Checks durchführen.

        // Beispiel: Immer OK zurückgeben (200 OK)
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        // Hier können Sie die Logik für den Readiness-Check implementieren
        // Zum Beispiel können Sie überprüfen, ob wichtige Ressourcen geladen sind
        // oder andere kritische Checks durchführen.

        // Beispiel: Immer OK zurückgeben (200 OK)
        return ResponseEntity.ok("OK");
    }
}
