package in.anujjamdade.groceryapi.security;

import java.io.IOException;
import java.util.Collections;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import in.anujjamdade.groceryapi.util.CookieUtil;
import in.anujjamdade.groceryapi.util.JwtUtil;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Skip public endpoints
        if (path.equals("/api/user/login") || path.equals("/api/user/logout")) {
            return true;
        }
        // Skip seller-specific order endpoints
        if (path.startsWith("/api/order/seller") || path.equals("/api/order/test-payment")) {
            return true;
        }
        // Apply this filter to user endpoints, address endpoints, cart endpoints, and order endpoints
        return !path.startsWith("/api/user") && !path.startsWith("/api/address")
            && !path.startsWith("/api/cart") && !path.startsWith("/api/order");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        System.out.println("UserAuthenticationFilter processing: " + path);

        try {
            Cookie cookie = cookieUtil.getCookie(request, "GROCERY_AUTH");

            if (cookie != null && cookie.getValue() != null) {
                String token = cookie.getValue();
                System.out.println("Token found in cookie: " + token.substring(0, Math.min(20, token.length())) + "...");

                if (jwtUtil.isTokenValid(token)) {
                    String role = jwtUtil.getRoleFromToken(token);
                    System.out.println("Token is valid, role: " + role);

                    // Only allow USER role for user endpoints
                    if ("USER".equals(role)) {
                        String subject = jwtUtil.getSubjectFromToken(token);
                        System.out.println("Setting authentication for user: " + subject);

                        UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                subject,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                            );

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        System.out.println("Authentication set successfully with ROLE_USER");
                    } else {
                        System.out.println("Role is not USER, skipping authentication");
                    }
                } else {
                    System.out.println("Token is invalid");
                }
            } else {
                System.out.println("No cookie found");
            }
        } catch (Exception e) {
            System.err.println("Error in UserAuthenticationFilter: " + e.getMessage());
            e.printStackTrace();
            // Clear authentication on any error
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
