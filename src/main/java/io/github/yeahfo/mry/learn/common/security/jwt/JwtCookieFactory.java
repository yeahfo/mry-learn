package io.github.yeahfo.mry.learn.common.security.jwt;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.AUTH_COOKIE_NAME;
import static java.util.Arrays.asList;

@Component
public class JwtCookieFactory {
    private final Environment environment;
    private final int expire;//分钟
    @Value( "${spring.security.oauth2.authorizationserver.cookie-setup-domain}" )
    private String cookieSetupDomain;

    public JwtCookieFactory( Environment environment,
                             JwtService jwtService) {
        this.environment = environment;
        this.expire = jwtService.getExpire();
    }
    public Cookie newJwtCookie( String jwt) {
        String[] activeProfiles = environment.getActiveProfiles();


        if (asList(activeProfiles).contains("prod")) {
            return newProdCookie(jwt);
        }

        return newNonProdCookie(jwt);
    }

    private Cookie newNonProdCookie(String jwt) {
        Cookie cookie = new Cookie(AUTH_COOKIE_NAME, jwt);
        cookie.setMaxAge(expire * 60);
        cookie.setPath("/");
        cookie.setDomain(cookieSetupDomain);
        return cookie;
    }

    private Cookie newProdCookie(String jwt) {
        Cookie cookie = new Cookie(AUTH_COOKIE_NAME, jwt);
        cookie.setMaxAge(expire * 60);
        cookie.setDomain(cookieSetupDomain);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }

    public Cookie logoutCookie() {
        Cookie cookie = new Cookie(AUTH_COOKIE_NAME, "");
        cookie.setMaxAge(0);
        cookie.setDomain(cookieSetupDomain);
        cookie.setPath("/");
        return cookie;
    }
}
