package in.anujjamdade.groceryapi.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Data;

@Data
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    @NotEmpty(message = "Product description is required")
    private List<String> description;

    @NotNull(message = "Product price is required")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotNull(message = "Offer price is required")
    @Positive(message = "Offer price must be positive")
    private Double offerPrice;

    @NotBlank(message = "Category is required")
    private String category;
}

