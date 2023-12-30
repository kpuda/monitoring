package dev.kpuda.persistence.service;

import dev.kpuda.persistence.entity.User;
import dev.kpuda.persistence.repository.UserRepository;
import io.micrometer.core.annotation.Timed;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

@Timed
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger("persistence.api.service");
    private final Random random;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.random = new Random();
        this.userRepository = userRepository;
    }

    public User getUser(Long id) {
        sleep(id);
        return userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User with given id doesn't exists"));
    }

    @PostConstruct
    void run() {
        userRepository.saveAll(List.of(
                new User("Andrew", "Johnson", "Mechanic"),
                new User("Emily", "Connor", "Assistant"),
                new User("Carson", "Storch", "Athlete"),
                new User("Joshua", "Weisman", "Cook")
        ));
    }

    private void sleep(Long id) {
        try {
            long sleepTime = random.nextLong(5000L);
            log.info("Persistence request te fetch user with id <{}> with additional sleep of {} ms", id, sleepTime);
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
        }
    }
}