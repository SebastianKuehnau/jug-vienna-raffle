package com.vaadin.demo.application.data;

import com.vaadin.demo.application.repository.RaffleRepository;
import com.vaadin.demo.application.repository.SamplePersonRepository;
import com.vaadin.demo.application.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Service for initializing sample data that can be triggered via HTTP
 */
@Service
@RequiredArgsConstructor
public class SampleDataService {

    private final SamplePersonRepository samplePersonRepository;
    private final UserRepository userRepository;
    private final RaffleRepository raffleRepository;

    /**
     * Load sample data into the database
     */
    @Transactional
    public void loadSampleData() {
        // Only load data if the sample people table is empty
        if (samplePersonRepository.count() == 0) {
            loadSamplePeople();
        }

        // Only load users if the users table is empty
        if (userRepository.count() == 0) {
            loadSampleUsers();
        }

        // Only load raffles if the raffle table is empty
        if (raffleRepository.count() == 0) {
            loadSampleRaffles();
        }
    }

    private void loadSamplePeople() {
        // Create and save sample persons
        SamplePerson person1 = new SamplePerson();
        person1.setFirstName("Eula");
        person1.setLastName("Lane");
        person1.setEmail("eula.lane@jigrormo.ye");
        person1.setPhone("(762) 526-5961");
        person1.setDateOfBirth(LocalDate.of(1955, 8, 7));
        person1.setOccupation("Insurance Clerk");
        person1.setRole("Worker");
        person1.setImportant(false);
        samplePersonRepository.save(person1);

        SamplePerson person2 = new SamplePerson();
        person2.setFirstName("Barry");
        person2.setLastName("Rodriquez");
        person2.setEmail("barry.rodriquez@zun.mm");
        person2.setPhone("(267) 955-5124");
        person2.setDateOfBirth(LocalDate.of(2014, 8, 7));
        person2.setOccupation("Mortarman");
        person2.setRole("Manager");
        person2.setImportant(false);
        samplePersonRepository.save(person2);
    }

    private void loadSampleUsers() {
        // Create and save user credentials
        User user = new User();
        user.setUsername("user");
        user.setName("John Normal");
        user.setHashedPassword("$2a$10$xdbKoM48VySZqVSU/cSlVeJn0Z04XCZ7KZBjUBC00eKo5uLswyOpe");
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(Role.USER);
        user.setRoles(userRoles);
        userRepository.save(user);

        User admin = new User();
        admin.setUsername("admin");
        admin.setName("Emma Executive");
        admin.setHashedPassword("$2a$10$jpLNVNeA7Ar/ZQ2DKbKCm.MuT2ESe.Qop96jipKMq7RaUgCoQedV.");
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(Role.USER);
        adminRoles.add(Role.ADMIN);
        admin.setRoles(adminRoles);
        userRepository.save(admin);
    }

    private void loadSampleRaffles() {
        // Sample raffle data
        Raffle raffle1 = createRaffle("305897255");
        Raffle raffle2 = createRaffle("305897281");
        Raffle raffle3 = createRaffle("306898838");
        
        raffleRepository.save(raffle1);
        raffleRepository.save(raffle2);
        raffleRepository.save(raffle3);
    }

    private Raffle createRaffle(String meetupEventId) {
        Raffle raffle = new Raffle();
        raffle.setMeetup_event_id(meetupEventId);
        return raffle;
    }
}