package ra.security.jwt;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ra.security.user_principal.UserPrinciple;


import java.util.Date;

@Component
public class JwtProvider {
//    JwtProvider: Lớp này chịu trách nhiệm tạo và kiểm tra tính hợp lệ của các token JWT.
//    Nó có các phương thức để tạo token (generateToken),
//    kiểm tra tính hợp lệ của token (validateToken),
//    và trích xuất tên người dùng từ token (getUserNameFromToken).
//    Lớp này sử dụng thư viện io.jsonwebtoken để làm việc với JWT.
    public final Logger logger = LoggerFactory.getLogger(JwtEntryPoint.class);
    @Value("${jwt.secret-key}")
    private String SECRET;
    @Value("${jwt.expirated}")
    private Long EXPIRED;

    public String generateToken(UserPrinciple userPrinciple) {
        return Jwts.builder().setSubject(userPrinciple.getUsername()) // set chủ đề
                .setIssuedAt(new Date()) // Thời gian bắt đầu
                .setExpiration(new Date(new Date().getTime() + EXPIRED)) // thời gian kết thúc
                .signWith(SignatureAlgorithm.HS512, SECRET) // chuwx kí và thuật toán mã hóa , chuỗi bí mật
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token); //Lưu ý chính tả
            return true;

        } catch (ExpiredJwtException e) {
            logger.error("Failed -> Expired Token Message {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Failed -> Unsupported Token Message {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Failed -> Invalid Format Token Message {}", e.getMessage());
        } catch (SignatureException e) {
            logger.error("Failed -> Invalid Signature Token Message {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Failed -> Claims Empty Token Message {}", e.getMessage());
        }
        return false;
    }

    public String getUserNameFromToken(String token) {
        return Jwts.parser().setSigningKey(SECRET)
                .parseClaimsJws(token).getBody().getSubject(); // lưu ý chính tả
    }

}
