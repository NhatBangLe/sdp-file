package io.github.nhatbangle.sdp.file.helper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Slf4j
@Validated
public final class JwtHelper {

    public static final String ISSUER = "SDP-FILE";
    public static final SecretKey secretKey = Jwts.SIG.HS256.key().build(); //or HS384.key() or HS512.key();

    @NotNull
    public static String createToken(@NotNull Map<String, String> claims) {
        var nowMillis = System.currentTimeMillis();
        var now = new Date(nowMillis);
        var expMillis = nowMillis + 3600000; // 1 hour expiration
        var exp = new Date(expMillis);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .issuer(ISSUER)
                .expiration(exp)
                .signWith(secretKey).compact();
    }

    public static boolean verifyToken(@NotNull String token) {
        var parser = Jwts.parser().verifyWith(secretKey).build();
        try {
            return parser.parse(token) != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Nullable
    public static Claims getClaims(@NotNull String token) {
        if (!verifyToken(token)) return null;
        try {
            return Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            return null;
        }
    }

}
