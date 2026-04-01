package in.anujjamdade.groceryapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestOrderResponse {
    private Boolean success;
    private String message;
    private OrderDetails order;

    @Data
    @AllArgsConstructor
    public static class OrderDetails {
        private String id;
        private Double amount;
        private Boolean isPaid;
    }
}

