package fr.nil.backedflow.config;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    /**
     * The JWT authentication filter.
     */
private final JWTAuthenticationFilter jwtAuthFilter;
    /**
     * The authentication provider.
     */
private final AuthenticationProvider authenticationProvider;

    /**
     * Configures the security filter chain for HTTP requests.
     *
     * @param httpSecurity the HTTP security object to configure
     * @return the security filter chain
     * @throws Exception if an error occurs while configuring the security filter chain
     */

    /*
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, CorsConfigurationSource corsConfigurationSource) throws Exception {
        httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/api/v1/auth/**", "/api/v1/folder/download/**", "/api/v1/verify/**", "/v3/api-docs/**", "/swagger-ui/**")
                        .permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }


     */
    @Value("${transferflow.security.actuator.password:password123!}")
    private String actuatorPassword;


    /**
     * Configures the CORS configuration for HTTP requests.
     *
     * @return the CORS configuration source
     */
    // ! Need to be configured for production this is highly unsecure practice
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://192.168.140.15:9006", "http://192.168.140.15:4200", "http://192.168.140.15", "http://192.168.1.18:4200", "http://api.transfer-flow.studio", "https://api.transfer-flow.studio", "http://transfer-flow.studio", "https://transfer-flow.studio")); // allow all origins
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "HEAD")); // allow all HTTP methods
        configuration.setAllowedHeaders(Arrays.asList("*")); // allow all headers
        configuration.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin", "Access-Control-Allow-Methods", "Access-Control-Allow-Headers", "Access-Control-Max-Age")); // expose additional headers
        configuration.setAllowCredentials(true); // allow cookies
        configuration.setMaxAge(3600L); // set max age

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }

    /**
     * Configures the security filter chain for HTTP requests.
     *
     * @param httpSecurity the HTTP security object to configure
     * @return the security filter chain
     * @throws Exception if an error occurs while configuring the security filter chain
     **/

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors()
                .and()
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/auth/**", "/api/v1/folder/download/**", "/api/v1/verify/**")
                .permitAll()
                .requestMatchers("/actuator/**")
                .permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

        ;

        return httpSecurity.build();
    }
/*
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("prometheus")
                .password(new BCryptPasswordEncoder().encode(actuatorPassword))
                .authorities("ROLE_ADMIN");
    }


 */
}
