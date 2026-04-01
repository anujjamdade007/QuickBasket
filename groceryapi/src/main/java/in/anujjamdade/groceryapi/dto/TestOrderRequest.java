package in.anujjamdade.groceryapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TestOrderRequest {

    @NotBlank(message = "orderDbId is required")
    private String orderDbId;

    @NotBlank(message = "userId is required")
    private String userId;
}

