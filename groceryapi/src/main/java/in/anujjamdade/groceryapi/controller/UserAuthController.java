package in.anujjamdade.groceryapi.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.anujjamdade.groceryapi.dto.LoginRequest;
import in.anujjamdade.groceryapi.model.User;
import in.anujjamdade.groceryapi.service.JwtService;
import in.anujjamdade.groceryapi.service.impl.UserServiceImpl;
import in.anujjamdade.groceryapi.util.CookieUtil;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserAuthController {

    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CookieUtil cookieUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        User user = userService.findByEmail(request.getEmail());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            Map<String, Object> body = new HashMap<>();
            body.put("success", false);
            body.put("message", "Bad Credentials");
            return ResponseEntity.status(401).body(body);
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");
        String token = jwtService.generateToken(user.getId(), claims);

        cookieUtil.create(response, "GROCERY_AUTH", token, false, 24 * 60 * 60);

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", user.getEmail());
        userMap.put("name", user.getName());
        userMap.put("_id", user.getId());
        body.put("user", userMap);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/is-auth")
    public ResponseEntity<?> isAuthenticated(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, Object> body = new HashMap<>();
            body.put("success", false);
            body.put("message", "Not authorized");
            return ResponseEntity.status(401).body(body);
        }

        // Get user ID from authentication (subject in JWT token)
        String userId = authentication.getName();
        User user = userService.findById(userId);

        if (user == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("success", false);
            body.put("message", "User not found");
            return ResponseEntity.status(404).body(body);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", user.getName());
        userMap.put("_id", user.getId());
        userMap.put("email", user.getEmail());

        // Add cart items if they exist
        if (user.getCartItems() != null && !user.getCartItems().isEmpty()) {
            userMap.put("cartItems", user.getCartItems());
        } else {
            userMap.put("cartItems", new HashMap<>());
        }

        body.put("user", userMap);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        cookieUtil.clear(response, "GROCERY_AUTH");

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("message", "Logged Out");
        return ResponseEntity.ok(body);
    }
}

