package com.example.studentagent.controller;

import com.example.studentagent.dto.FilterRequest;
import com.example.studentagent.entity.Student;
import com.example.studentagent.repository.StudentRepository;
import com.example.studentagent.repository.StudentSpecification;
import com.example.studentagent.service.GeminiAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentRepository repository;
    private final GeminiAgentService geminiService;

    // 1. API: Pass raw filter manually
    @PostMapping("/filter")
    public Page<Student> filterStudents(@RequestBody FilterRequest filter,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "5") int size) {
        return repository.findAll(
                StudentSpecification.getSpecification(filter),
                PageRequest.of(page, size)
        );
    }

    // 2. API: Pass Natural Language
    @PostMapping("/ask")
    public Page<Student> askAgent(@RequestBody String question,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "5") int size) {

        System.out.println("User asked: " + question);

        // Step A: Get Filter from Gemini
        FilterRequest filter = geminiService.getFilterFromNaturalLanguage(question);

        System.out.println("Gemini generated filter: " + filter);

        // Step B: Query DB
        return repository.findAll(
                StudentSpecification.getSpecification(filter),
                PageRequest.of(page, size)
        );
    }

    // 3. Load Dummy Data on Startup
    @Bean
    public CommandLineRunner loadData(StudentRepository repo) {
        return args -> {
            repo.saveAll(List.of(
                    new Student(null, "John Doe", "Computer Science", 20, 3.8, LocalDate.now().minusYears(1)),
                    new Student(null, "Jane Smith", "Mathematics", 22, 3.9, LocalDate.now().minusYears(2)),
                    new Student(null, "Mike Brown", "Physics", 21, 2.5, LocalDate.now().minusYears(1)),
                    new Student(null, "Sarah Connor", "Computer Science", 19, 4.0, LocalDate.now()),
                    new Student(null, "Bruce Wayne", "Engineering", 25, 3.2, LocalDate.now().minusYears(3))
            ));
        };
    }
}
