package in.anujjamdade.groceryapi.dto;

import java.time.Instant;
import java.util.List;

import lombok.Data;

@Data
public class UserOrderDto {
    private String id;
    private String userId;
    private List<OrderItemDto> items;
    private Double amount;
    private AddressDetailsDto address;
    private String status;
    private String paymentType;
    private Boolean isPaid;
    private Instant createdAt;

    @Data
    public static class OrderItemDto {
        private ProductDetailsDto product;
        private Integer quantity;
    }

    @Data
    public static class ProductDetailsDto {
        private String id;
        private String name;
        private List<String> image;
        private Double offerPrice;
        private String category;
    }
}

