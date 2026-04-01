package in.anujjamdade.groceryapi.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.anujjamdade.groceryapi.dto.OrderRequest;
import in.anujjamdade.groceryapi.dto.OrderResponse;
import in.anujjamdade.groceryapi.dto.RazorpayOrderResponse;
import in.anujjamdade.groceryapi.dto.StripeOrderResponse;
import in.anujjamdade.groceryapi.dto.TestOrderRequest;
import in.anujjamdade.groceryapi.dto.TestOrderResponse;
import in.anujjamdade.groceryapi.dto.UserOrderListResponse;
import in.anujjamdade.groceryapi.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/cod")
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody OrderRequest request) {
        try {
            OrderResponse response = orderService.placeOrder(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new OrderResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new OrderResponse(false, "Error placing order: " + e.getMessage()));
        }
    }

    @PostMapping("/razorpay")
    public ResponseEntity<?> createRazorpayOrder(@Valid @RequestBody OrderRequest request) {
        try {
            RazorpayOrderResponse response = orderService.createRazorpayOrder(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error creating order: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/stripe")
    public ResponseEntity<?> createStripeOrder(@Valid @RequestBody OrderRequest request) {
        try {
            StripeOrderResponse response = orderService.createStripeOrder(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error creating order: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/test-payment")
    public ResponseEntity<?> testOrderPayment(@Valid @RequestBody TestOrderRequest request) {
        try {
            TestOrderResponse response = orderService.testOrderPayment(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error processing test payment: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserOrders(
            Authentication authentication,
            @RequestParam(required = false) Boolean isPaid) {
        try {
            // Get userId from authentication
            String userId = authentication.getName();

            UserOrderListResponse response = orderService.getUserOrders(userId, isPaid);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error fetching orders: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/seller")
    public ResponseEntity<?> getSellerOrders(@RequestParam(required = false) Boolean isPaid) {
        try {
            UserOrderListResponse response = orderService.getSellerOrders(isPaid);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error fetching orders: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}

