package fr.nil.backedflow.config;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
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
    private final Environment env;


    @Value("${transferflow.security.actuator.password:password123!}")
    private String actuatorPassword;
    /**
     * Configures the CORS configuration for HTTP requests.
     *
     * @return the CORS configuration source
     */

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "https://api-azure.transfer-flow.studio", "https://azure.transfer-flow.studio", "http://api.transfer-flow.studio", "https://api.transfer-flow.studio", "http://transfer-flow.studio", "https://transfer-flow.studio")); // allow all origins
        configuration.setAllowedMethods(Arrays.asList("GET", "PATCH", "OPTIONS", "POST", "PUT", "DELETE", "HEAD")); // allow all HTTP methods
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
     * @param http the HTTP security object to configure

     * @param mvc  the MVC request matcher builder
     * @return the security filter chain
     * @throws Exception if an error occurs while configuring the security filter chain
     **/
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .addFilterAfter(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(authorizedRequests -> {
                    authorizedRequests
                            .requestMatchers(mvc.pattern("/assets/**")).permitAll()
                            .requestMatchers(mvc.pattern("/api/v1/auth/**"), mvc.pattern("/api/v1/folder/download/**"), mvc.pattern("/api/v1/folder/url/**"), mvc.pattern("/api/v1/verify/**")).permitAll()
                            .requestMatchers(mvc.pattern("/actuator/**")).permitAll()
                            .requestMatchers(mvc.pattern("/v3/api-docs/**"), mvc.pattern("/swagger-ui/**")).permitAll();

                    if (env.acceptsProfiles(Profiles.of("apitesting"))) {
                        authorizedRequests.requestMatchers(mvc.pattern("/api/v1/admin/**")).permitAll();
                    } else {
                        authorizedRequests.requestMatchers(mvc.pattern("/api/v1/admin/**")).hasAuthority("ADMIN");
                    }

                    authorizedRequests.anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer
                                .authenticationEntryPoint((request, response, authException) -> response.sendError(403, "Forbidden, authentication exception"))

                                .accessDeniedHandler((request, response, accessDeniedException) -> response.sendError(403, "Forbidden"))
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }
}

