package com.ipr.websocket.service.websocket;

import com.ipr.websocket.mongo.User;
import com.ipr.websocket.mongo.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoScheduler {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void runScheduler() {
        User user = new User();
        user.setName("Vasya");
        user.setAge(30);
        userRepository.save(user);

        User user2 = new User();
        user2.setName("Vasya2");
        user2.setAge(30);
        userRepository.save(user2);

        User user3 = new User();
        user3.setName("Vasya3");
        user3.setAge(30);
        userRepository.save(user3);

        // 2. Получаем всех пользователей
        List<User> users = userRepository.findAll();
        System.out.println("All users: " + users);

        // Пример запроса с limit и skip
        Query query = new Query()
                .limit(2) // Ограничение 10 записей
                .with(Sort.by(Sort.Direction.DESC, "age")); // Сортировка по возрасту
        List<User> usersList = mongoTemplate.find(query, User.class);


        System.out.println("Scheduler finished.");
    }
}
