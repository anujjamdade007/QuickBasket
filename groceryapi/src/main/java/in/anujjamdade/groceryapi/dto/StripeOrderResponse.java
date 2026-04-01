package in.anujjamdade.groceryapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StripeOrderResponse {
    private Boolean success;
    private String url;
}

