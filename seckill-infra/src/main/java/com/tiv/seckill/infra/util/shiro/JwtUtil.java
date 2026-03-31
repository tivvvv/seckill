package com.tiv.seckill.infra.util.shiro;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.tiv.seckill.domain.constants.Constants;

import java.util.Date;

/**
 * JWT 工具类
 */
public class JwtUtil {

    /**
     * 校验 token
     *
     * @param token
     * @param secret
     * @return
     */
    public static boolean verify(String token, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim(Constants.TOKEN_CLAIM, getUserId(token))
                    .build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    /**
     * 从token中获取用户 id
     *
     * @param token
     * @return
     */
    public static Long getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(Constants.TOKEN_CLAIM).asLong();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 生成 token
     *
     * @param userId
     * @return
     */
    public static String sign(Long userId) {
        return sign(userId, Constants.JWT_SECRET);
    }

    /**
     * 生成 token
     *
     * @param userId
     * @param secret
     * @return
     */
    public static String sign(Long userId, String secret) {
        Date date = new Date(System.currentTimeMillis() + Constants.TOKEN_EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withClaim(Constants.TOKEN_CLAIM, userId)
                .withExpiresAt(date)
                .sign(algorithm);
    }

}