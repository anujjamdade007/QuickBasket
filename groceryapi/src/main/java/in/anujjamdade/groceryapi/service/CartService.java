package in.anujjamdade.groceryapi.service;

import in.anujjamdade.groceryapi.dto.CartUpdateRequest;
import in.anujjamdade.groceryapi.dto.CartUpdateResponse;

public interface CartService {
    CartUpdateResponse updateCart(CartUpdateRequest request);
}

