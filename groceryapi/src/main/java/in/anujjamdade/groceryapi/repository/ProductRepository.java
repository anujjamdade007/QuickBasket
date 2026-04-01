package in.anujjamdade.groceryapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import in.anujjamdade.groceryapi.model.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
}

