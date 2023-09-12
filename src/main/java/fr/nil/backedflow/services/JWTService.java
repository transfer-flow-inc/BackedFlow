package fr.nil.backedflow.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    @Value("${transferflow.api.token.secret.key}")
    public String secretKey;
    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);


    public String extractUsernameFromToken(String jwtToken)
    {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }


    public String generateToken(UserDetails userDetails)
    {
        return generateToken(new HashMap<>(),userDetails);
    }

    public String generateToken(Map<String, Object> claims, UserDetails userDetails)
    {
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (1000*60)*60*5)) // 5 Hours
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String jwtToken) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    public boolean isTokenValid(String token, UserDetails userDetails, HttpServletRequest request) {
        try {
            final String username = extractUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (ExpiredJwtException expiredJwtException) {
            logger.info("Utils :: validateToke :: token Exception -> expired!");
            request.setAttribute("expired", expiredJwtException.getMessage());
            throw new ExpiredJwtException(expiredJwtException.getHeader(), expiredJwtException.getClaims(), "Expired JWT token");
        }

    }

    private Date getExpirationDate(String jwtToken)
    {
        return extractClaim(jwtToken,Claims::getExpiration);
    }
    public boolean isTokenExpired(String token) {
        return getExpirationDate(token).before(new Date());
    }

    public Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
