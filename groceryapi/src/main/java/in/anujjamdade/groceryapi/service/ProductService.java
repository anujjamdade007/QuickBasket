package in.anujjamdade.groceryapi.service;

import org.springframework.web.multipart.MultipartFile;

import in.anujjamdade.groceryapi.dto.ProductRequest;
import in.anujjamdade.groceryapi.dto.ProductResponse;
import in.anujjamdade.groceryapi.dto.ProductListResponse;
import in.anujjamdade.groceryapi.dto.ProductDetailResponse;

import java.util.List;

public interface ProductService {
    ProductResponse addProduct(ProductRequest request, List<MultipartFile> images);
    ProductListResponse getAllProducts();
    ProductDetailResponse getProductById(String id);
    ProductResponse updateProductStock(String id, Boolean inStock);
}

