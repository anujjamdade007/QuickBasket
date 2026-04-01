package in.anujjamdade.groceryapi.service;

import in.anujjamdade.groceryapi.dto.UserRegisterRequest;
import in.anujjamdade.groceryapi.dto.UserResponse;

public interface UserService {
    UserResponse register(UserRegisterRequest request);
}

