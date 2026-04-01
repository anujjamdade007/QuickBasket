package in.anujjamdade.groceryapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductStockRequest {

    @NotBlank(message = "Product ID is required")
    private String id;

    @NotNull(message = "Stock status is required")
    private Boolean inStock;
}

