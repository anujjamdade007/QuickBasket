package in.anujjamdade.groceryapi.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserOrderListResponse {
    private Boolean success;
    private List<UserOrderDto> orders;
}

