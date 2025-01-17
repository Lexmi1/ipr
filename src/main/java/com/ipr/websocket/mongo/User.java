package com.ipr.websocket.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "employe")
public class User {

    @Id
    private String id;
    private String name;
    private int age;
}
