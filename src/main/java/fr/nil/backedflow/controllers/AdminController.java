package fr.nil.backedflow.controllers;


import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {


    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        Page<User> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserByID(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(adminService.getUserByID(id));
    }

}
