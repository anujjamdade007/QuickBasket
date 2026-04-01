package in.anujjamdade.groceryapi.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import in.anujjamdade.groceryapi.dto.CartUpdateRequest;
import in.anujjamdade.groceryapi.dto.CartUpdateResponse;
import in.anujjamdade.groceryapi.model.User;
import in.anujjamdade.groceryapi.repository.UserRepository;
import in.anujjamdade.groceryapi.service.CartService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final UserRepository userRepository;

    @Override
    public CartUpdateResponse updateCart(CartUpdateRequest request) {
        // Find user by ID
        Optional<User> userOptional = userRepository.findById(request.getUserId());

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOptional.get();

        // Update the cart items
        user.setCartItems(request.getCartItems());

        // Save the updated user
        userRepository.save(user);

        return new CartUpdateResponse(true, "Cart Updated");
    }
}

