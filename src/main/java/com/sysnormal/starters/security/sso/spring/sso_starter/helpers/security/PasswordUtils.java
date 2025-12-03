package com.sysnormal.starters.security.sso.spring.sso_starter.helpers.security;

import com.sysnormal.starters.security.sso.spring.sso_starter.properties.security.SecurityProperties;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PasswordUtils {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+[{]}|;:,.<>?";

    private static final SecureRandom random = new SecureRandom();

    /**
     * Gera uma senha que respeita as regras configuradas e futuras.
     *
     * @param email email do usuário (para evitar partes do e-mail na senha)
     * @param rules propriedades com as regras (mínimo, necessidade de caracteres especiais etc)
     * @return senha aleatória válida
     */
    public static String generateCompliantPassword(String email, SecurityProperties.PasswordRules rules) {
        int minLength = Optional.ofNullable(rules.getMinLength()).orElse(8);
        boolean requireUppercase = Optional.ofNullable(rules.getRequireUppercase()).orElse(true);
        boolean requireLowercase = Optional.ofNullable(rules.getRequireLowercase()).orElse(true);
        boolean requireDigits = Optional.ofNullable(rules.getRequireDigits()).orElse(true);
        boolean requireSpecial = Optional.ofNullable(rules.getRequireSpecial()).orElse(false);

        String allChars = "";
        if (requireUppercase) allChars += UPPER;
        if (requireLowercase) allChars += LOWER;
        if (requireDigits) allChars += DIGITS;
        if (requireSpecial) allChars += SPECIAL;

        if (allChars.isEmpty()) {
            allChars = UPPER + LOWER + DIGITS; // fallback
        }

        String password;
        do {
            password = buildPassword(minLength, requireUppercase, requireLowercase, requireDigits, requireSpecial, allChars);
        } while (!isPasswordCompliant(password, email, rules));

        return password;
    }

    private static String buildPassword(
            int length,
            boolean requireUppercase,
            boolean requireLowercase,
            boolean requireDigits,
            boolean requireSpecial,
            String allChars) {

        List<Character> chars = new ArrayList<>();

        if (requireUppercase) chars.add(randomChar(UPPER));
        if (requireLowercase) chars.add(randomChar(LOWER));
        if (requireDigits) chars.add(randomChar(DIGITS));
        if (requireSpecial) chars.add(randomChar(SPECIAL));

        // completa até o tamanho mínimo
        while (chars.size() < length) {
            chars.add(randomChar(allChars));
        }

        // embaralha pra não ter padrão
        Collections.shuffle(chars, random);

        return chars.stream().map(String::valueOf).collect(Collectors.joining());
    }

    private static char randomChar(String pool) {
        return pool.charAt(random.nextInt(pool.length()));
    }

    private static boolean isPasswordCompliant(String password, String email, SecurityProperties.PasswordRules rules) {
        // 1. comprimento
        if (password.length() < rules.getMinLength()) return false;

        // 2. partes do email
        if (email != null && !email.isBlank()) {
            String localPart = email.split("@")[0].toLowerCase();
            if (password.toLowerCase().contains(localPart)) return false;
        }

        // 3. presença de categorias obrigatórias
        if (rules.getRequireUppercase() && password.chars().noneMatch(Character::isUpperCase)) return false;
        if (rules.getRequireLowercase() && password.chars().noneMatch(Character::isLowerCase)) return false;
        if (rules.getRequireDigits() && password.chars().noneMatch(Character::isDigit)) return false;
        if (rules.getRequireSpecial() && password.chars().noneMatch(ch -> SPECIAL.indexOf(ch) >= 0)) return false;

        // regras futuras (poderia ler via lambda/listeners)
        return true;
    }
}
