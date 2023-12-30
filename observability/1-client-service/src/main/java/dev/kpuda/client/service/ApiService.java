package dev.kpuda.client.service;

import dev.kpuda.client.entity.User;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Service
@Observed(name = "client.service.custom.observation")
public class ApiService {

    private static final Logger log = LoggerFactory.getLogger("client.api.service");

    private final String userServiceUrl;

    private final Random random;
    private final RestTemplate restTemplate;
    private final Counter successfulRequestsCounter;
    private final Counter unsuccessfulRequestsCounter;

    public ApiService(RestTemplate restTemplate, @Value("${user.service.url}") String userServiceUrl) {
        this.random = new Random();
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
        this.successfulRequestsCounter = Metrics.counter("successful.requests");
        this.unsuccessfulRequestsCounter = Metrics.counter("unsuccessful.requests");
    }

    @Timed(value = "additional.observation.timed.client.method", histogram = true)
    public User getUserFromPersistanceService(int userId) {
        sleep(userId);
        try {
            User user = restTemplate.getForObject(userServiceUrl + "/persistence/user/" + userId, User.class);
            handleSuccessfullResponse(user);
            return user;
        } catch (RestClientException e) {
            handleUnSuccessfulResponse(e);
            return null;
        }
    }

    private void handleSuccessfullResponse(User user) {
        successfulRequestsCounter.increment();
        log.info("Successful response no. <{}>: {}", successfulRequestsCounter.count(), user);
    }

    private void handleUnSuccessfulResponse(Exception exception) {
        unsuccessfulRequestsCounter.increment();
        log.error("Error from api: {}", exception.getMessage());
    }

    @Scheduled(fixedRateString = "PT45S")
    public void sendRequests() {
        for (int i = 0; i < random.nextInt(25); i++) {
            int userId = random.nextInt(6);
            new Thread(() -> log.info("Response {}", restTemplate.getForObject("http://localhost:8080/client/user/" + userId, String.class))).start();
            sleep(userId);
        }
    }

    private void sleep(int userId) {
        try {
            long sleepTime = random.nextLong(5000L);
            log.info("Sending request to persistence-service to find user by userId: <{}> with additional sleep of {} ms", userId, sleepTime);
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}