package in.anujjamdade.groceryapi.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import in.anujjamdade.groceryapi.dto.ProductDetailResponse;
import in.anujjamdade.groceryapi.dto.ProductDto;
import in.anujjamdade.groceryapi.dto.ProductListResponse;
import in.anujjamdade.groceryapi.dto.ProductRequest;
import in.anujjamdade.groceryapi.dto.ProductResponse;
import in.anujjamdade.groceryapi.exception.ProductNotFoundException;
import in.anujjamdade.groceryapi.model.Product;
import in.anujjamdade.groceryapi.repository.ProductRepository;
import in.anujjamdade.groceryapi.service.CloudinaryService;
import in.anujjamdade.groceryapi.service.ProductService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public ProductResponse addProduct(ProductRequest request, List<MultipartFile> images) {
        // Upload images to Cloudinary
        List<String> imageUrls = cloudinaryService.uploadImages(images);

        // Create product entity
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOfferPrice(request.getOfferPrice());
        product.setCategory(request.getCategory());
        product.setImage(imageUrls);
        product.setInStock(true);

        // Save product
        productRepository.save(product);

        return new ProductResponse(true, "Product Added");
    }

    @Override
    public ProductListResponse getAllProducts() {
        List<Product> products = productRepository.findAll();

        List<ProductDto> productDtos = products.stream()
            .map(product -> {
                ProductDto dto = new ProductDto();
                dto.setId(product.getId());
                dto.setName(product.getName());
                dto.setDescription(product.getDescription());
                dto.setPrice(product.getPrice());
                dto.setOfferPrice(product.getOfferPrice());
                dto.setImage(product.getImage());
                dto.setCategory(product.getCategory());
                dto.setInStock(product.getInStock());
                return dto;
            })
            .collect(Collectors.toList());

        return new ProductListResponse(productDtos, true);
    }

    @Override
    public ProductDetailResponse getProductById(String id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setOfferPrice(product.getOfferPrice());
        productDto.setImage(product.getImage());
        productDto.setCategory(product.getCategory());
        productDto.setInStock(product.getInStock());

        return new ProductDetailResponse(productDto, true);
    }

    @Override
    public ProductResponse updateProductStock(String id, Boolean inStock) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        product.setInStock(inStock);
        productRepository.save(product);

        return new ProductResponse(true, "Stock status updated");
    }
}

