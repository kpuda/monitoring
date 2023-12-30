package dev.kpuda.persistence.controller;

import dev.kpuda.persistence.entity.User;
import dev.kpuda.persistence.service.UserService;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@Observed(name = "persistence.http.controller")
@RequestMapping("persistence")
@RequiredArgsConstructor
public class Controller {

    private final UserService userService;

    @GetMapping("user/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUser(id);

    }

    @ExceptionHandler(NoSuchElementException.class)
    public ProblemDetail noSuchElementException(Exception exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }
}
