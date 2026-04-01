package in.anujjamdade.groceryapi.model;

import java.time.Instant;
import java.util.Map;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "users")
@Data
public class User {

    @Id
    private String id;

    private String name;

    private String email;

    private String password;

    // Map of productId -> CartItem
    private Map<String, CartItem> cartItems;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    // CartItem as inner static class
    @Data
    public static class CartItem {
        private Integer quantity;
    }
}

