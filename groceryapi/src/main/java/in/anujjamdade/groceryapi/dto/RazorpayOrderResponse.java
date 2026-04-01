package in.anujjamdade.groceryapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RazorpayOrderResponse {
    private Boolean success;
    private Integer amount;
    private String currency;
    private String keyId;
    private String orderDbId;
    private String orderId;
}

