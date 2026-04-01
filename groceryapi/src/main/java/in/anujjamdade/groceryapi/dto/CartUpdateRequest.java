package in.anujjamdade.groceryapi.dto;

import java.util.Map;

import in.anujjamdade.groceryapi.model.User.CartItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartUpdateRequest {

    @NotBlank(message = "userId is required")
    private String userId;

    @NotNull(message = "cartItems is required")
    private Map<String, CartItem> cartItems;
}

