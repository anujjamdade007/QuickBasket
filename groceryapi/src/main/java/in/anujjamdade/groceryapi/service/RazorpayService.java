package in.anujjamdade.groceryapi.service;

import org.json.JSONObject;

public interface RazorpayService {
    JSONObject createOrder(Integer amount, String currency) throws Exception;
}

