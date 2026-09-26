package notesapi.security;
import io.jsonwebtoken.Claims;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
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

   @Nested
   @DisplayName("extractUsername()")
   class ExtractUsername{
      @Test
      @DisplayName("deve extrair o email do token gerado")
      void deveExtrairEmailDoToken(){
         String token = jwtService.generateToken(userEntity);
         String username = jwtService.extractUsername(token);
         assertThat(username).isEqualTo("joao@teste.com");
      }
   }

   @Nested
   @DisplayName("isTokenExpired()")
   class IsTokenExpired {

      @Test
      @DisplayName("deve retornar false para token recém-gerado")
      void deveRetornarFalse_paraTokenRecemGerado(){
         String token = jwtService.generateToken(userEntity);
         Boolean expirado = jwtService.isTokenExpired(token);
         assertThat(expirado).isFalse();
      }

      @Test
      @DisplayName("deve retornar true para token expirado")
      void deveRetornarTrue_paraTokenExpirado() {
         long agora = System.currentTimeMillis();
         long umDiaAtras = agora - (1000L * 60*60 *24);

         String tokenExpirado = Jwts.builder()
             .subject(userEntity.getUsername())
             .issuedAt(new Date(umDiaAtras))
             .expiration(new Date(umDiaAtras))
             .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_FAKE)))
             .compact();

         Boolean expirado = jwtService.isTokenExpired(tokenExpirado);
         assertThat(expirado).isTrue();
      }
   }

   private Claims parseClaims(String token){
      return Jwts.parser()
          .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_FAKE)))
          .build()
          .parseSignedClaims(token)
          .getPayload();
   }

}