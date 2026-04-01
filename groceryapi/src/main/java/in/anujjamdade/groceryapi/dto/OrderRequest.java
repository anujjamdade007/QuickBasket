package in.anujjamdade.groceryapi.dto;

import java.util.List;

import in.anujjamdade.groceryapi.model.Order.OrderItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {

    @NotBlank(message = "userId is required")
    private String userId;

    @NotBlank(message = "address is required")
    private String address;

    @NotNull(message = "items are required")
    private List<OrderItem> items;
}

