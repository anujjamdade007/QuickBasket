package in.anujjamdade.groceryapi.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import in.anujjamdade.groceryapi.service.JwtService;
import in.anujjamdade.groceryapi.util.JwtUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtUtil jwtUtil;

    @Override
    public String generateToken(String subject, Map<String, Object> claims) {
        return jwtUtil.generateToken(subject, claims);
    }

    @Override
    public boolean validateToken(String token) {
        return jwtUtil.isTokenValid(token);
    }
}

