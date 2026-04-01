package in.anujjamdade.groceryapi.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private String name;
    private List<String> description;
    private Double price;
    private Double offerPrice;
    private List<String> image;
    private String category;
    private Boolean inStock;

    @JsonProperty("_id")
    private String id;
}

