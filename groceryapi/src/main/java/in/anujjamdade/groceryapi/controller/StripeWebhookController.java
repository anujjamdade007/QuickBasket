package in.anujjamdade.groceryapi.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

import in.anujjamdade.groceryapi.service.OrderService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StripeWebhookController {

    private final OrderService orderService;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/stripe")
    public ResponseEntity<?> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            System.err.println("⚠️  Webhook signature verification failed: " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid signature");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // Deserialize the nested object inside the event
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            System.err.println("⚠️  Deserialization failed, possibly due to API version mismatch");
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Deserialization failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // Handle the event
        switch (event.getType()) {
            case "payment_intent.succeeded":
                handlePaymentIntentSucceeded(stripeObject);
                break;
            case "payment_intent.payment_failed":
                handlePaymentIntentFailed(stripeObject);
                break;
            case "checkout.session.completed":
                handleCheckoutSessionCompleted(stripeObject);
                break;
            default:
                System.out.println("Unhandled event type: " + event.getType());
        }

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    private void handlePaymentIntentSucceeded(StripeObject stripeObject) {
        PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
        System.out.println("✅ Payment succeeded for PaymentIntent: " + paymentIntent.getId());

        // Extract metadata if available
        if (paymentIntent.getMetadata() != null && paymentIntent.getMetadata().containsKey("orderDbId")) {
            String orderDbId = paymentIntent.getMetadata().get("orderDbId");
            orderService.updateOrderPaymentStatus(orderDbId, true, "Payment Completed");
            System.out.println("Updated order " + orderDbId + " to paid status");
        }
    }

    private void handlePaymentIntentFailed(StripeObject stripeObject) {
        PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
        System.out.println("❌ Payment failed for PaymentIntent: " + paymentIntent.getId());

        // Extract metadata if available
        if (paymentIntent.getMetadata() != null && paymentIntent.getMetadata().containsKey("orderDbId")) {
            String orderDbId = paymentIntent.getMetadata().get("orderDbId");
            orderService.updateOrderPaymentStatus(orderDbId, false, "Payment Failed");
            System.out.println("Updated order " + orderDbId + " to failed status");
        }
    }

    private void handleCheckoutSessionCompleted(StripeObject stripeObject) {
        Session session = (Session) stripeObject;
        System.out.println("✅ Checkout session completed: " + session.getId());

        // Extract orderDbId from metadata
        if (session.getMetadata() != null && session.getMetadata().containsKey("orderDbId")) {
            String orderDbId = session.getMetadata().get("orderDbId");

            // Check if payment was successful
            if ("paid".equals(session.getPaymentStatus())) {
                orderService.updateOrderPaymentStatus(orderDbId, true, "Payment Completed");
                System.out.println("Updated order " + orderDbId + " to paid status");
            }
        }
    }
}

