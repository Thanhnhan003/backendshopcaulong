package com.nguyenthanhnhan.backendshopcaulong.config;

import com.nguyenthanhnhan.backendshopcaulong.filter.JwtAuthFilter;
import com.nguyenthanhnhan.backendshopcaulong.service.jwt.UserInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter authFilter;

    // User Creation
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserInfoService();
    }

    // Password Encoding
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://192.168.1.4:3000");
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://localhost:3001");
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    // Configuring HttpSecurity
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers("/auth/welcome", "/auth/register", "/auth/login").permitAll())
                // jwt
                .authorizeHttpRequests(auth -> auth.requestMatchers("/auth/show/**").hasAnyAuthority("USER", "ADMIN"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/auth/user/**").hasAuthority("USER"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/auth/all").hasAuthority("ADMIN"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/auth/info").hasAnyAuthority("USER", "ADMIN"))

                .authorizeHttpRequests(auth -> auth.requestMatchers("/auth/enabled/**").hasAuthority("ADMIN"))

                .authorizeHttpRequests(auth -> auth.requestMatchers("/auth/loginadmin").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/auth/forgot_password").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/auth/activate").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/auth/avatar").hasAnyAuthority("USER", "ADMIN"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/auth/reset-password").hasAnyAuthority("USER", "ADMIN"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/auth/update").hasAnyAuthority("USER", "ADMIN"))

                .authorizeHttpRequests(auth -> auth.requestMatchers("/auth/images/**").permitAll())

                .authorizeHttpRequests(auth -> auth.requestMatchers("/auth/login/admin").hasAuthority("ADMIN"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/auth/admin/**").hasAuthority("ADMIN"))
                // news
                .authorizeHttpRequests(auth -> auth.requestMatchers("/news/show/**").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/news/slug/**").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/news/images/**").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/news/**").hasAuthority("ADMIN"))
                 // brand
                 .authorizeHttpRequests(auth -> auth.requestMatchers("/brand/show/**").permitAll())
                 .authorizeHttpRequests(auth -> auth.requestMatchers("/brand/slug/**").permitAll())
                 .authorizeHttpRequests(auth -> auth.requestMatchers("/brand/images/**").permitAll())
                 .authorizeHttpRequests(auth -> auth.requestMatchers("/brand/**").hasAuthority("ADMIN"))
                // category
                .authorizeHttpRequests(auth -> auth.requestMatchers("/category/show/**").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/category/slug/**").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/category/images/**").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/category/**").hasAuthority("ADMIN"))
                // products
                .authorizeHttpRequests(auth -> auth.requestMatchers("/products/show/**").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/products/categories-brands").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/products/similar/**").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/products/slug/**").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/products/latest/**").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/products/images/**").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/products/delete/**").hasAuthority("ADMIN"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/products/update/**").hasAuthority("ADMIN"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/products/create/**").hasAuthority("ADMIN"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/products/**").permitAll())
                // order
                .authorizeHttpRequests(auth -> auth.requestMatchers("/orders/add/**").hasAnyAuthority("USER", "ADMIN"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/orders/payment").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/orders/users").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("**").permitAll())
                // cartitems
                .authorizeHttpRequests(auth -> auth.requestMatchers("/cart/add").hasAnyAuthority("USER", "ADMIN"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/cart/remove/**").hasAnyAuthority("USER", "ADMIN"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/cart/items").permitAll())
                //address
                .authorizeHttpRequests(auth -> auth.requestMatchers("/address/add").hasAnyAuthority("USER", "ADMIN"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/address/delete").hasAnyAuthority("USER", "ADMIN"))
                // comment
                .authorizeHttpRequests(auth -> auth.requestMatchers("/comments/add").hasAnyAuthority("USER", "ADMIN"))
                //upload img
                .authorizeHttpRequests(auth -> auth.requestMatchers("/upload/image").hasAnyAuthority("USER", "ADMIN"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/upload/images").permitAll())

                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
