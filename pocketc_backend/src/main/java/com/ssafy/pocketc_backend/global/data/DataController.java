package com.ssafy.pocketc_backend.global.data;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/data")
@AllArgsConstructor
public class DataController {

    private final DataService dataService;

    @DeleteMapping("/reset")
    public ResponseEntity<?> deleteAll() {
        dataService.deleteAll();
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/delete/last-event")
    public ResponseEntity<?> deleteLastEvent() {
        dataService.deleteLastEvent();
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/all")
    public ResponseEntity<?> putAllData() {
        dataService.putAllData();
        return ResponseEntity.ok(HttpStatus.OK);
    }
}