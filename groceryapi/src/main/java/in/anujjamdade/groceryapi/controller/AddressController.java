package in.anujjamdade.groceryapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.anujjamdade.groceryapi.dto.AddressListResponse;
import in.anujjamdade.groceryapi.dto.AddressRequest;
import in.anujjamdade.groceryapi.dto.AddressResponse;
import in.anujjamdade.groceryapi.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/add")
    public ResponseEntity<AddressResponse> addAddress(
            @Valid @RequestBody AddressRequest request,
            Authentication authentication) {
        try {
            // Get userId from authentication
            String userId = authentication.getName();

            AddressResponse response = addressService.addAddress(userId, request.getAddress());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AddressResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/get")
    public ResponseEntity<AddressListResponse> getUserAddresses(Authentication authentication) {
        try {
            // Get userId from authentication
            String userId = authentication.getName();

            AddressListResponse response = addressService.getUserAddresses(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AddressListResponse(false, null));
        }
    }
}

