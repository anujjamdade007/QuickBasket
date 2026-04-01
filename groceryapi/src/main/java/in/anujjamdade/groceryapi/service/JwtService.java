package in.anujjamdade.groceryapi.service;

import java.util.Map;

public interface JwtService {
    String generateToken(String subject, Map<String, Object> claims);
    boolean validateToken(String token);
}

