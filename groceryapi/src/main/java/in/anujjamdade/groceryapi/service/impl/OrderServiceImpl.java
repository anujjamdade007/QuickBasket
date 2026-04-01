package in.anujjamdade.groceryapi.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.model.checkout.Session;

import in.anujjamdade.groceryapi.dto.AddressDetailsDto;
import in.anujjamdade.groceryapi.dto.OrderRequest;
import in.anujjamdade.groceryapi.dto.OrderResponse;
import in.anujjamdade.groceryapi.dto.RazorpayOrderResponse;
import in.anujjamdade.groceryapi.dto.StripeOrderResponse;
import in.anujjamdade.groceryapi.dto.TestOrderRequest;
import in.anujjamdade.groceryapi.dto.TestOrderResponse;
import in.anujjamdade.groceryapi.dto.UserOrderDto;
import in.anujjamdade.groceryapi.dto.UserOrderListResponse;
import in.anujjamdade.groceryapi.model.Address;
import in.anujjamdade.groceryapi.model.Order;
import in.anujjamdade.groceryapi.model.Product;
import in.anujjamdade.groceryapi.model.User;
import in.anujjamdade.groceryapi.repository.AddressRepository;
import in.anujjamdade.groceryapi.repository.OrderRepository;
import in.anujjamdade.groceryapi.repository.ProductRepository;
import in.anujjamdade.groceryapi.repository.UserRepository;
import in.anujjamdade.groceryapi.service.OrderService;
import in.anujjamdade.groceryapi.service.RazorpayService;
import in.anujjamdade.groceryapi.service.StripeService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final RazorpayService razorpayService;
    private final StripeService stripeService;

    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    @Override
    public OrderResponse placeOrder(OrderRequest request) {
        // Validate user exists
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validate address exists
        addressRepository.findById(request.getAddress())
            .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        // Calculate total amount
        double totalAmount = 0.0;
        for (Order.OrderItem item : request.getItems()) {
            Product product = productRepository.findById(item.getProduct())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.getProduct()));

            // Use offer price if available, otherwise use regular price
            double price = product.getOfferPrice() != null ? product.getOfferPrice() : product.getPrice();
            totalAmount += price * item.getQuantity();
        }

        // Create order
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setAddress(request.getAddress());
        order.setItems(request.getItems());
        order.setAmount(totalAmount);
        order.setStatus("Order Placed");
        order.setPaymentType("COD");
        order.setIsPaid(false);

        // Save order
        orderRepository.save(order);

        return new OrderResponse(true, "Order Placed Successfully");
    }

    @Override
    public RazorpayOrderResponse createRazorpayOrder(OrderRequest request) throws Exception {
        // Validate user exists
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validate address exists
        addressRepository.findById(request.getAddress())
            .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        // Calculate total amount
        double totalAmount = 0.0;
        for (Order.OrderItem item : request.getItems()) {
            Product product = productRepository.findById(item.getProduct())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.getProduct()));

            // Use offer price if available, otherwise use regular price
            double price = product.getOfferPrice() != null ? product.getOfferPrice() : product.getPrice();
            totalAmount += price * item.getQuantity();
        }

        // Convert amount to paise (1 rupee = 100 paise)
        int amountInPaise = (int) Math.round(totalAmount * 100);

        // Create Razorpay order
        JSONObject razorpayOrder = razorpayService.createOrder(amountInPaise, "INR");
        String razorpayOrderId = razorpayOrder.getString("id");

        // Create order in database
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setAddress(request.getAddress());
        order.setItems(request.getItems());
        order.setAmount(totalAmount);
        order.setStatus("Order Placed");
        order.setPaymentType("Razorpay");
        order.setIsPaid(false);

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Return response
        return new RazorpayOrderResponse(
            true,
            amountInPaise,
            "INR",
            razorpayKeyId,
            savedOrder.getId(),
            razorpayOrderId
        );
    }

    @Override
    public StripeOrderResponse createStripeOrder(OrderRequest request) throws Exception {
        // Validate user exists
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validate address exists
        addressRepository.findById(request.getAddress())
            .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        // Calculate total amount
        double totalAmount = 0.0;
        for (Order.OrderItem item : request.getItems()) {
            Product product = productRepository.findById(item.getProduct())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.getProduct()));

            // Use offer price if available, otherwise use regular price
            double price = product.getOfferPrice() != null ? product.getOfferPrice() : product.getPrice();
            totalAmount += price * item.getQuantity();
        }

        // Create order in database first
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setAddress(request.getAddress());
        order.setItems(request.getItems());
        order.setAmount(totalAmount);
        order.setStatus("Pending Payment");
        order.setPaymentType("Stripe");
        order.setIsPaid(false);

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Create Stripe checkout session
        Session session = stripeService.createCheckoutSession(totalAmount, "INR", savedOrder.getId());

        // Return response with checkout URL
        return new StripeOrderResponse(true, session.getUrl());
    }

    @Override
    public void updateOrderPaymentStatus(String orderId, boolean isPaid, String status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.setIsPaid(isPaid);
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Override
    public TestOrderResponse testOrderPayment(TestOrderRequest request) {
        // Validate user exists
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Find the order
        Order order = orderRepository.findById(request.getOrderDbId())
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // Verify order belongs to user
        if (!order.getUserId().equals(request.getUserId())) {
            throw new IllegalArgumentException("Order does not belong to this user");
        }

        // Update order payment status
        order.setIsPaid(true);
        order.setStatus("Payment Completed");
        Order updatedOrder = orderRepository.save(order);

        // Determine message based on payment type
        String message = "TEST: ";
        if ("COD".equalsIgnoreCase(updatedOrder.getPaymentType())) {
            message += "COD order marked as paid successfully";
        } else if ("Razorpay".equalsIgnoreCase(updatedOrder.getPaymentType())) {
            message += "Razorpay payment completed successfully";
        } else if ("Stripe".equalsIgnoreCase(updatedOrder.getPaymentType())) {
            message += "Stripe payment completed successfully";
        } else {
            message += "Payment completed successfully";
        }

        // Create order details
        TestOrderResponse.OrderDetails orderDetails = new TestOrderResponse.OrderDetails(
            updatedOrder.getId(),
            updatedOrder.getAmount(),
            updatedOrder.getIsPaid()
        );

        return new TestOrderResponse(true, message, orderDetails);
    }

    @Override
    public UserOrderListResponse getUserOrders(String userId, Boolean isPaid) {
        // Validate user exists
        userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Fetch orders based on isPaid filter
        List<Order> orders;
        if (isPaid != null) {
            orders = orderRepository.findByUserIdAndIsPaid(userId, isPaid);
        } else {
            orders = orderRepository.findByUserId(userId);
        }

        // Convert orders to DTOs
        List<UserOrderDto> orderDtos = orders.stream()
            .map(this::convertToUserOrderDto)
            .collect(Collectors.toList());

        return new UserOrderListResponse(true, orderDtos);
    }

    private UserOrderDto convertToUserOrderDto(Order order) {
        UserOrderDto dto = new UserOrderDto();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setAmount(order.getAmount());
        dto.setStatus(order.getStatus());
        dto.setPaymentType(order.getPaymentType());
        dto.setIsPaid(order.getIsPaid());
        dto.setCreatedAt(order.getCreatedAt());

        // Convert address
        Address address = addressRepository.findById(order.getAddress())
            .orElse(null);
        if (address != null) {
            AddressDetailsDto addressDto = new AddressDetailsDto();
            addressDto.setId(address.getId());
            addressDto.setFirstName(address.getFirstName());
            addressDto.setLastName(address.getLastName());
            addressDto.setEmail(address.getEmail());
            addressDto.setStreet(address.getStreet());
            addressDto.setCity(address.getCity());
            addressDto.setState(address.getState());
            addressDto.setZipcode(address.getZipcode());
            addressDto.setCountry(address.getCountry());
            addressDto.setPhone(address.getPhone());
            dto.setAddress(addressDto);
        }

        // Convert items
        List<UserOrderDto.OrderItemDto> itemDtos = order.getItems().stream()
            .map(item -> {
                UserOrderDto.OrderItemDto itemDto = new UserOrderDto.OrderItemDto();
                itemDto.setQuantity(item.getQuantity());

                // Fetch product details
                Product product = productRepository.findById(item.getProduct())
                    .orElse(null);
                if (product != null) {
                    UserOrderDto.ProductDetailsDto productDto = new UserOrderDto.ProductDetailsDto();
                    productDto.setId(product.getId());
                    productDto.setName(product.getName());
                    productDto.setImage(product.getImage());
                    productDto.setOfferPrice(product.getOfferPrice());
                    productDto.setCategory(product.getCategory());
                    itemDto.setProduct(productDto);
                }

                return itemDto;
            })
            .collect(Collectors.toList());

        dto.setItems(itemDtos);
        return dto;
    }

    @Override
    public UserOrderListResponse getSellerOrders(Boolean isPaid) {
        // Fetch all orders based on isPaid filter
        List<Order> orders;
        if (isPaid != null) {
            orders = orderRepository.findByIsPaid(isPaid);
        } else {
            orders = orderRepository.findAll();
        }

        // Convert orders to DTOs using the same conversion method
        List<UserOrderDto> orderDtos = orders.stream()
            .map(this::convertToUserOrderDto)
            .collect(Collectors.toList());

        return new UserOrderListResponse(true, orderDtos);
    }
}

