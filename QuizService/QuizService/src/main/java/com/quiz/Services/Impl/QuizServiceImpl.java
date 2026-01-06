package com.quiz.Services.Impl;

import com.quiz.Services.QuestionClient;
import com.quiz.Services.QuizService;
import com.quiz.entities.Question;
import com.quiz.entities.Quiz;
import com.quiz.repositories.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuestionClient questionClient;

    public QuizServiceImpl(QuestionClient questionClient, QuizRepository quizRepository) {
        this.questionClient = questionClient;
        this.quizRepository = quizRepository;
    }

    @Override
    public Quiz add(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    @Override
    public List<Quiz> get() {
        List<Quiz> quizzes = quizRepository.findAll();

        // Fetch questions synchronously for each quiz
        return quizzes.stream().map(quiz -> {
            List<Question> questions = questionClient.getQuestionsOfQuiz(quiz.getId());
            quiz.setQuestions(questions);
            return quiz;
        }).collect(Collectors.toList());
    }

    @Override
    public Quiz get(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found !!"));

        // Fetch questions synchronously
        List<Question> questions = questionClient.getQuestionsOfQuiz(quiz.getId());
        quiz.setQuestions(questions);

        return quiz;
    }
}