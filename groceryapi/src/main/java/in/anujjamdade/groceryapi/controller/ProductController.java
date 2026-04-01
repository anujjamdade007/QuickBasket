package in.anujjamdade.groceryapi.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import in.anujjamdade.groceryapi.dto.ProductDetailResponse;
import in.anujjamdade.groceryapi.dto.ProductIdRequest;
import in.anujjamdade.groceryapi.dto.ProductListResponse;
import in.anujjamdade.groceryapi.dto.ProductRequest;
import in.anujjamdade.groceryapi.dto.ProductResponse;
import in.anujjamdade.groceryapi.dto.ProductStockRequest;
import in.anujjamdade.groceryapi.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @GetMapping("/list")
    public ResponseEntity<ProductListResponse> getAllProducts() {
        ProductListResponse response = productService.getAllProducts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/id")
    public ResponseEntity<ProductDetailResponse> getProductById(@RequestParam("id") String id) {
        ProductDetailResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/id")
    public ResponseEntity<ProductDetailResponse> getProductByIdPost(@Valid @RequestBody ProductIdRequest request) {
        ProductDetailResponse response = productService.getProductById(request.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> addProduct(
            @RequestParam("productData") String productDataJson,
            @RequestParam("images") List<MultipartFile> images) {

        try {
            // Trim and clean the JSON string
            String cleanJson = productDataJson.trim();

            // Log for debugging
            System.out.println("Received productData: " + cleanJson);

            // Validate images first
            if (images == null || images.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ProductResponse(false, "At least one image is required"));
            }

            // Parse the JSON string to ProductRequest
            ProductRequest productRequest = objectMapper.readValue(cleanJson, ProductRequest.class);

            // Validate the parsed request
            if (productRequest.getName() == null || productRequest.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ProductResponse(false, "Product name is required"));
            }

            ProductResponse response = productService.addProduct(productRequest, images);
            return ResponseEntity.ok(response);
        } catch (JsonProcessingException e) {
            String errorMsg = "Invalid JSON format. Please check: " + e.getOriginalMessage();
            System.err.println("JSON Parse Error: " + errorMsg);
            System.err.println("Received data: " + productDataJson);
            return ResponseEntity.badRequest()
                .body(new ProductResponse(false, errorMsg));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ProductResponse(false, "Validation error: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(new ProductResponse(false, "Failed to add product: " + e.getMessage()));
        }
    }

    @PostMapping("/stock")
    public ResponseEntity<ProductResponse> updateProductStock(@Valid @RequestBody ProductStockRequest request) {
        try {
            ProductResponse response = productService.updateProductStock(request.getId(), request.getInStock());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ProductResponse(false, e.getMessage()));
        }
    }
}

