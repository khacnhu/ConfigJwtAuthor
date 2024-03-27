package com.example.JwtAuthor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DetailController {

    @GetMapping("/details")
    public ResponseEntity<String> details() {
        return ResponseEntity.ok("Login successfully to join details page");
    }

    @GetMapping("/admin_only")
    public ResponseEntity<String> adminOnly() {
        return ResponseEntity.ok("Hello login successfully for admin page");
    }



}
