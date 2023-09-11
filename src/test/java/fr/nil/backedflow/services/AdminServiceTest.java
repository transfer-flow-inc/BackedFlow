package fr.nil.backedflow.services;

import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.entities.user.UserTicket;
import fr.nil.backedflow.repositories.UserRepository;
import fr.nil.backedflow.repositories.UserTicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserTicketRepository userTicketRepository;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        User user = User.builder().id(UUID.randomUUID()).firstName("test").lastName("test").build();

    }

    @Test
    public void testGetAllUsers() {
        Pageable pageable = Pageable.ofSize(20);
        Page<User> users = mock(Page.class);
        when(userRepository.findAll(pageable)).thenReturn(users);

        adminService.getAllUsers(pageable);

        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testGetUserByID() {
        UUID id = UUID.randomUUID();
        User user = mock(User.class);
        when(userRepository.findUserById(id)).thenReturn(Optional.of(user));

        adminService.getUserByID(id);

        verify(userRepository, times(1)).findUserById(id);
    }

    @Test
    public void testGetAllTickets() {
        Pageable pageable = Pageable.ofSize(20);
        Page<UserTicket> tickets = mock(Page.class);
        when(userTicketRepository.findAll(pageable)).thenReturn(tickets);

        adminService.getAllTickets(pageable);

        verify(userTicketRepository, times(1)).findAll(pageable);
    }
}


