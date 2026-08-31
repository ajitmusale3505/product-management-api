package com.zestindia.productmanagement.config;

import com.zestindia.productmanagement.security.CustomUserDetailsService;
import com.zestindia.productmanagement.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                .csrf(AbstractHttpConfigurer::disable)

                .formLogin(
                        AbstractHttpConfigurer::disable
                )

                .httpBasic(
                        AbstractHttpConfigurer::disable
                )

                .sessionManagement(
                        session -> session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(
                        authorization -> authorization

                                /*
                                 * Public endpoints
                                 */
                                .requestMatchers(
                                        "/api/v1/auth/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/v3/api-docs/**"
                                )
                                .permitAll()

                                /*
                                 * ADMIN can create products
                                 */
                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/v1/products"
                                )
                                .hasRole("ADMIN")

                                /*
                                 * ADMIN can update products
                                 */
                                .requestMatchers(
                                        HttpMethod.PUT,
                                        "/api/v1/products/**"
                                )
                                .hasRole("ADMIN")

                                /*
                                 * ADMIN can delete products
                                 */
                                .requestMatchers(
                                        HttpMethod.DELETE,
                                        "/api/v1/products/**"
                                )
                                .hasRole("ADMIN")

                                /*
                                 * ADMIN can create items
                                 */
                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/v1/products/*/items"
                                )
                                .hasRole("ADMIN")

                                /*
                                 * ADMIN and USER can view products
                                 */
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/v1/products/**"
                                )
                                .hasAnyRole(
                                        "ADMIN",
                                        "USER"
                                )

                                /*
                                 * Everything else requires authentication
                                 */
                                .anyRequest()
                                .authenticated()
                )

                .userDetailsService(
                        userDetailsService
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}