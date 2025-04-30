package com.hello.ai.chataibackend.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {
    private final StringRedisTemplate redisTemplate;
    private static final String CODE_PREFIX = "verification_code:";
    private static final long CODE_EXPIRE_MINUTES = 1; // 验证码1分钟有效期

    public VerificationCodeService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 生成并存储验证码
     * @param phone 手机号
     * @return 生成的验证码
     */
    public String generateAndSaveCode(String phone) {
        String code = generateCode();
        String key = CODE_PREFIX + phone;
        redisTemplate.opsForValue().set(key, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        return code;
    }

    /**
     * 验证验证码
     * @param phone 手机号
     * @param code 验证码
     * @return 是否验证通过
     */
    public boolean verifyCode(String phone, String code) {
        String key = CODE_PREFIX + phone;
        String savedCode = redisTemplate.opsForValue().get(key);
        if (savedCode != null && savedCode.equals(code)) {
            redisTemplate.delete(key); // 验证通过后删除验证码
            return true;
        }
        return false;
    }

    /**
     * 生成6位数字验证码
     */
    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
} 