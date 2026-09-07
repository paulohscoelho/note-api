package notesapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
@Service
public class JwtService {
    @Value("${api.security.token.secret}")
    String secretKey;

    private SecretKey getSigningKey(){
      byte[] keyBytes = Decoders.BASE64.decode(secretKey);
      return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email){
        long now = System.currentTimeMillis();
        long expirationTime = 1000L * 60 * 60 * 24;
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    public String extractUsername(String token){
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public Boolean isTokenExpired(String token){
        Claims claims = extractAllClaims(token);
        return claims.getExpiration().before(new Date());
    }
}
