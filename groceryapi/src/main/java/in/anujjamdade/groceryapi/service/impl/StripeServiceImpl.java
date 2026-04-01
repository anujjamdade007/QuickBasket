package in.anujjamdade.groceryapi.service.impl;

import org.springframework.stereotype.Service;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import in.anujjamdade.groceryapi.service.StripeService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {

    @Override
    public Session createCheckoutSession(Double amount, String currency, String orderDbId) throws Exception {
        // Convert amount to cents/paise (Stripe uses smallest currency unit)
        long amountInCents = Math.round(amount * 100);

        SessionCreateParams params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl("http://localhost:3000/order/success?session_id={CHECKOUT_SESSION_ID}")
            .setCancelUrl("http://localhost:3000/order/cancel")
            .addLineItem(
                SessionCreateParams.LineItem.builder()
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency(currency.toLowerCase())
                            .setUnitAmount(amountInCents)
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName("Grocery Order")
                                    .build()
                            )
                            .build()
                    )
                    .setQuantity(1L)
                    .build()
            )
            .putMetadata("orderDbId", orderDbId)
            .build();

        return Session.create(params);
    }
}

