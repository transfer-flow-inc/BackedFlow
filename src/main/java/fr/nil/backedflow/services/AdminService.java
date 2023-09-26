package fr.nil.backedflow.services;

import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.entities.user.UserTicket;
import fr.nil.backedflow.repositories.FolderRepository;
import fr.nil.backedflow.repositories.UserRepository;
import fr.nil.backedflow.repositories.UserTicketRepository;
import fr.nil.backedflow.services.folder.FolderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository userRepository;
    private final UserTicketRepository userTicketRepository;
    private final FolderRepository folderRepository;
    private final FolderService folderService;

    //add pagination to this 20 user per page

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User getUserByID(UUID id) {
        return userRepository.findUserById(id).orElseThrow();

    }

    public void deleteUserByID(UUID id) {
        userRepository.deleteById(id);
    }

    public void deleteFolderByID(UUID id) {
        folderService.deleteFolder(folderRepository.findById(id).orElseThrow());
    }

    public Page<Folder> getAllFolderFromUserID(UUID id, Pageable pageable) {
        return folderRepository.findAllByFolderOwnerId(id, pageable);
    }

    public Folder getFolderByID(UUID id) {
        return folderRepository.findById(id).orElseThrow();
    }


    public Page<UserTicket> getAllTickets(Pageable pageable) {
        return userTicketRepository.findAll(pageable);
    }

    public UserTicket getTicketByID(int id) {
        return userTicketRepository.findById(id).orElseThrow();
    }


}
