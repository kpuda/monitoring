package dev.kpuda.client.controller;

import dev.kpuda.client.service.ApiService;
import dev.kpuda.client.entity.User;
import io.micrometer.observation.annotation.Observed;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@Observed(name = "client.controller.custom.observation")
@RequestMapping("client")
@RequiredArgsConstructor
public class Controller {

    private final ApiService apiService;

    @GetMapping("user/{id}")
    public User getUserById(@PathVariable int id, HttpServletRequest request) {
        return apiService.getUserFromPersistanceService(id);
    }
}