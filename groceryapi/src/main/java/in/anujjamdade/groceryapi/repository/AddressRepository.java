package in.anujjamdade.groceryapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import in.anujjamdade.groceryapi.model.Address;

@Repository
public interface AddressRepository extends MongoRepository<Address, String> {
    Optional<Address> findByUserId(String userId);
    List<Address> findAllByUserId(String userId);
}

