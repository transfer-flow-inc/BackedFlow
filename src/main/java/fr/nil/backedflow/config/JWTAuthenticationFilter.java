package fr.nil.backedflow.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import fr.nil.backedflow.services.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

/**
 * A filter that intercepts incoming requests and extracts JWT tokens from the "Authorization" header.
 * It then validates the tokens and sets the authenticated user in the SecurityContextHolder if the token is valid.
 */

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

private final JWTService jwtService;
private final UserDetailsService userDetailsService;
private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);



    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
        final String authHeader = request.getHeader("Authorization");

        final String jwtToken;
        final String userMail;
        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the JWT token from the Authorization header
        jwtToken = authHeader.substring(7);
        userMail = jwtService.extractUsernameFromToken(jwtToken);
        if (userMail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userMail);
            // Check if the token is valid

            if (!jwtService.isTokenValid(jwtToken, userDetails,request))
                throw new CredentialsExpiredException("Invalid token.");
            // Set the authenticated user in the SecurityContextHolder
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        }

        filterChain.doFilter(request, response);


        } catch (Exception exception){
            exception.printStackTrace();
            logger.error("Error logging in : {} ", exception.getMessage());
            response.setHeader("error",exception.getMessage());
            response.setStatus(INTERNAL_SERVER_ERROR.value());
            Map<String,String > error = new HashMap<>();
            error.put("error_message",exception.getMessage());
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(),error);
        }
    }

}
