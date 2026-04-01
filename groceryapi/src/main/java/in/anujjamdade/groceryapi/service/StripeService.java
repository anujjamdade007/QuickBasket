package in.anujjamdade.groceryapi.service;

import com.stripe.model.checkout.Session;

public interface StripeService {
    Session createCheckoutSession(Double amount, String currency, String orderDbId) throws Exception;
}

