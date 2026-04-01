package in.anujjamdade.groceryapi.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import in.anujjamdade.groceryapi.security.SellerAuthenticationFilter;
import in.anujjamdade.groceryapi.security.UserAuthenticationFilter;
import in.anujjamdade.groceryapi.util.CookieUtil;
import in.anujjamdade.groceryapi.util.JwtUtil;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public UserAuthenticationFilter userAuthenticationFilter() {
        return new UserAuthenticationFilter(jwtUtil, cookieUtil);
    }

    @Bean
    public SellerAuthenticationFilter sellerAuthenticationFilter() {
        return new SellerAuthenticationFilter(jwtUtil, cookieUtil);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> {
                auth
                    // Public endpoints - most specific first
                    .requestMatchers("/api/users/register").permitAll()
                    .requestMatchers("/api/user/login").permitAll()
                    .requestMatchers("/api/user/logout").permitAll()
                    .requestMatchers("/api/seller/login").permitAll()
                    .requestMatchers("/api/seller/logout").permitAll()
                    .requestMatchers("/api/product/list").permitAll()
                    .requestMatchers("/api/product/id").permitAll()
                    .requestMatchers("/stripe").permitAll() // Stripe webhook
                    // Protected seller endpoints - before wildcard
                    .requestMatchers("/api/product/add").hasRole("SELLER")
                    .requestMatchers("/api/product/stock").hasRole("SELLER")
                    .requestMatchers("/api/order/seller").hasRole("SELLER")
                    .requestMatchers("/api/order/test-payment").hasRole("SELLER")
                    .requestMatchers("/api/seller/**").hasRole("SELLER")
                    // Protected user endpoints - after specific paths
                    .requestMatchers("/api/user/**").authenticated()
                    .requestMatchers("/api/address/**").authenticated()
                    .requestMatchers("/api/cart/**").authenticated()
                    .requestMatchers("/api/order/**").authenticated()
                    // All other requests are permitted
                    .anyRequest().permitAll();
            })
            .addFilterBefore(userAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(sellerAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(form -> form.disable())
            .logout(logout -> logout.disable());

        return http.build();
    }
}
