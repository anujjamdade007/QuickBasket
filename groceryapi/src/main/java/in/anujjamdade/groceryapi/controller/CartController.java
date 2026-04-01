package in.anujjamdade.groceryapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.anujjamdade.groceryapi.dto.CartUpdateRequest;
import in.anujjamdade.groceryapi.dto.CartUpdateResponse;
import in.anujjamdade.groceryapi.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/update")
    public ResponseEntity<CartUpdateResponse> updateCart(
            @Valid @RequestBody CartUpdateRequest request) {
        try {
            CartUpdateResponse response = cartService.updateCart(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new CartUpdateResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new CartUpdateResponse(false, "Error updating cart"));
        }
    }
}

