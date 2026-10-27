package com.quizapp.config;

import com.quizapp.entity.Option;
import com.quizapp.entity.Question;
import com.quizapp.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final QuestionRepository questionRepository;

    public DataInitializer(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (questionRepository.count() > 0) {
            log.info("Database already contains questions. Skipping sample data initialization.");
            return;
        }

        log.info("Initializing 10 sample multiple-choice questions with 4 options each...");

        List<Question> sampleQuestions = List.of(
                createQuestion(
                        "Which HTTP method is typically used to retrieve data from a specified resource without causing side effects?",
                        "Web Development",
                        List.of(
                                new Option("POST", false),
                                new Option("GET", true),
                                new Option("DELETE", false),
                                new Option("PATCH", false)
                        )
                ),
                createQuestion(
                        "In Java, which keyword is used to prevent a method from being overridden by a subclass?",
                        "Java",
                        List.of(
                                new Option("static", false),
                                new Option("abstract", false),
                                new Option("final", true),
                                new Option("synchronized", false)
                        )
                ),
                createQuestion(
                        "Which CSS property is used to create space between an element's border and its inner content?",
                        "CSS",
                        List.of(
                                new Option("margin", false),
                                new Option("padding", true),
                                new Option("border-spacing", false),
                                new Option("outline", false)
                        )
                ),
                createQuestion(
                        "What does SQL stand for in database management systems?",
                        "Databases",
                        List.of(
                                new Option("Structured Question Language", false),
                                new Option("Structured Query Language", true),
                                new Option("Simple Query Logic", false),
                                new Option("System Quality Language", false)
                        )
                ),
                createQuestion(
                        "In React, which hook is primarily used to perform side effects such as data fetching?",
                        "React",
                        List.of(
                                new Option("useState", false),
                                new Option("useEffect", true),
                                new Option("useContext", false),
                                new Option("useMemo", false)
                        )
                ),
                createQuestion(
                        "Which data structure operates on the Last In, First Out (LIFO) principle?",
                        "Data Structures",
                        List.of(
                                new Option("Queue", false),
                                new Option("Stack", true),
                                new Option("Binary Tree", false),
                                new Option("Linked List", false)
                        )
                ),
                createQuestion(
                        "In RESTful API design, which HTTP status code signifies that a resource was successfully created?",
                        "REST APIs",
                        List.of(
                                new Option("200 OK", false),
                                new Option("201 Created", true),
                                new Option("204 No Content", false),
                                new Option("301 Moved Permanently", false)
                        )
                ),
                createQuestion(
                        "What is the primary purpose of Spring Boot's application.properties or application.yml file?",
                        "Spring Boot",
                        List.of(
                                new Option("To compile Java source code into bytecode", false),
                                new Option("To configure externalized application and environment properties", true),
                                new Option("To render client-side HTML templates", false),
                                new Option("To manage operating system processes", false)
                        )
                ),
                createQuestion(
                        "In JavaScript, which operator checks for both equality of value and equality of type?",
                        "JavaScript",
                        List.of(
                                new Option("==", false),
                                new Option("===", true),
                                new Option("!=", false),
                                new Option("=", false)
                        )
                ),
                createQuestion(
                        "Which relational database constraint uniquely identifies each record in a table?",
                        "Databases",
                        List.of(
                                new Option("FOREIGN KEY", false),
                                new Option("PRIMARY KEY", true),
                                new Option("CHECK", false),
                                new Option("DEFAULT", false)
                        )
                )
        );

        questionRepository.saveAll(sampleQuestions);
        log.info("Successfully initialized {} sample quiz questions.", sampleQuestions.size());
    }

    private Question createQuestion(String text, String topic, List<Option> options) {
        Question q = new Question(text, topic);
        for (Option opt : options) {
            q.addOption(opt);
        }
        return q;
    }
}
