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
public class SellerAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Skip public endpoints
        if (path.equals("/api/seller/login") || path.equals("/api/seller/logout")) {
            return true;
        }
        // Apply this filter to seller endpoints and seller-protected product/order endpoints
        return !path.startsWith("/api/seller") &&
               !path.equals("/api/product/add") &&
               !path.equals("/api/product/stock") &&
               !path.equals("/api/order/seller") &&
               !path.equals("/api/order/test-payment");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            Cookie cookie = cookieUtil.getCookie(request, "GROCERY_AUTH");

            if (cookie != null && cookie.getValue() != null) {
                String token = cookie.getValue();

                if (jwtUtil.isTokenValid(token)) {
                    String role = jwtUtil.getRoleFromToken(token);

                    // Only allow SELLER role for seller endpoints
                    if ("SELLER".equals(role)) {
                        String subject = jwtUtil.getSubjectFromToken(token);

                        UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                subject,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_SELLER"))
                            );

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        } catch (Exception e) {
            // Clear authentication on any error
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
