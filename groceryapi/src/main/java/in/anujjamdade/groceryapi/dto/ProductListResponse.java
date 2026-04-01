package in.anujjamdade.groceryapi.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductListResponse {
    private List<ProductDto> products;
    private Boolean success;
}

