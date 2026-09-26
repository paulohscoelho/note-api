package notesapi.security;
import io.jsonwebtoken.Claims;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import notesapi.user.entity.Role;
import notesapi.user.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtService")
class JwtServiceTest {

   private JwtService jwtService;

   private static final String SECRET_FAKE =
       "dGVzdGUtc2VjcmV0LXRlc3RlLXNlY3JldC10ZXN0ZS1zZWNyZXQ=";

   private UserEntity userEntity;

   @BeforeEach
   void setUp() {
      jwtService = new JwtService();
      ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_FAKE);

      userEntity = new UserEntity();
      userEntity.setUuid(UUID.randomUUID());
      userEntity.setEmail("joao@teste.com");
      userEntity.setRole(Role.USER);
   }

   @Nested
   @DisplayName("generateToken()")
   class GerenateToken{
      @Test
      @DisplayName("deve gerar token com o email do usuario no subject")
      void deveGerarTokenComEmailNoSubject(){
         String token = jwtService.generateToken(userEntity);

         Claims claims = parseClaims(token);
         assertThat(claims.getSubject()).isEqualTo("joao@teste.com");
      }

      @Test
      @DisplayName("deve gerar token com o role do usuario")
      void deveGerarTokenComRoleDoUser(){
         String token = jwtService.generateToken(userEntity);

         Claims claims = parseClaims(token);
         assertThat(claims.get("role",String.class)).isEqualTo("USER");
      }
   }

   private Claims parseClaims(String token){
      return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_FAKE)))
          .build()
          .parseSignedClaims(token)
          .getPayload();
   }

}