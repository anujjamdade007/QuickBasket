package in.anujjamdade.groceryapi.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "addresses")
@Data
public class Address {

    @Id
    private String id;

    private String userId;

    private String firstName;

    private String lastName;

    private String email;

    private String street;

    private String city;

    private String state;

    private Integer zipcode;

    private String country;

    private String phone;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}

