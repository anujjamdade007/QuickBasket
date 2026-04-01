package in.anujjamdade.groceryapi.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import in.anujjamdade.groceryapi.model.Order;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserId(String userId);
    List<Order> findByUserIdAndIsPaid(String userId, Boolean isPaid);
    List<Order> findByIsPaid(Boolean isPaid);
}

