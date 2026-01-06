package com.quiz.Services;

import com.quiz.entities.Question;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

//@FeignClient(url = "http://localhost:8082", value = "Question-Client")
@FeignClient(name = "question-service")
public interface QuestionClient {

    // Synchronous call (no CompletableFuture)
    @GetMapping("/question/quiz/{quizId}")
    @CircuitBreaker(name = "questionServiceCB", fallbackMethod = "fallbackGetQuestions")
    @Retry(name = "questionServiceRetry")
    @RateLimiter(name = "questionServiceRL")
    List<Question> getQuestionsOfQuiz(@PathVariable("quizId") Long quizId);

    // Fallback method for CircuitBreaker
    default List<Question> fallbackGetQuestions(Long quizId, Throwable t) {
        System.out.println("Fallback triggered for quizId " + quizId + ": " + t.getMessage());
        return List.of(); // Return empty list if QuestionService fails
    }
}
