package fr.nil.backedflow.controllers;


import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.entities.user.UserTicket;
import fr.nil.backedflow.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    @GetMapping("folder/{id}")
    public ResponseEntity<Folder> getFolderByID(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(adminService.getFolderByID(id));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserByID(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(adminService.getUserByID(id));
    }

    @DeleteMapping("/user/{id}")
    public void deleteUserByID(@PathVariable("id") UUID id) {
        adminService.deleteUserByID(id);
    }

    @GetMapping("/user/{id}/folders")
    public ResponseEntity<Page<Folder>> getAllFolderFromUserID(@PathVariable("id") UUID id, Pageable pageable) {
        Page<Folder> folders = adminService.getAllFolderFromUserID(id, pageable);
        return ResponseEntity.ok(folders);
    }

    @GetMapping("/tickets")
    public ResponseEntity<Page<UserTicket>> getAllTickets(Pageable pageable) {
        Page<UserTicket> tickets = adminService.getAllTickets(pageable);
        return ResponseEntity.ok(tickets);
    }

}
