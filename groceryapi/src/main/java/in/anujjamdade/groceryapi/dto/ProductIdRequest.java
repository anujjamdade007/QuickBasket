package in.anujjamdade.groceryapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductIdRequest {

    @NotBlank(message = "Product ID is required")
    private String id;
}

