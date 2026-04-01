package in.anujjamdade.groceryapi.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "products")
@Data
public class Product {

    @Id
    private String id;

    private String name;

    private List<String> description;

    private Double price;

    private Double offerPrice;

    private List<String> image;

    private String category;

    private Boolean inStock = true;
}

