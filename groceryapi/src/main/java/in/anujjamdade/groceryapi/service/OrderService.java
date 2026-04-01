package in.anujjamdade.groceryapi.service;

import in.anujjamdade.groceryapi.dto.OrderRequest;
import in.anujjamdade.groceryapi.dto.OrderResponse;
import in.anujjamdade.groceryapi.dto.RazorpayOrderResponse;
import in.anujjamdade.groceryapi.dto.StripeOrderResponse;
import in.anujjamdade.groceryapi.dto.TestOrderRequest;
import in.anujjamdade.groceryapi.dto.TestOrderResponse;
import in.anujjamdade.groceryapi.dto.UserOrderListResponse;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest request);
    RazorpayOrderResponse createRazorpayOrder(OrderRequest request) throws Exception;
    StripeOrderResponse createStripeOrder(OrderRequest request) throws Exception;
    void updateOrderPaymentStatus(String orderId, boolean isPaid, String status);
    TestOrderResponse testOrderPayment(TestOrderRequest request);
    UserOrderListResponse getUserOrders(String userId, Boolean isPaid);
    UserOrderListResponse getSellerOrders(Boolean isPaid);
}

