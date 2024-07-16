package io.github.yeahfo.mry.learn.common.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URL;

import static com.google.common.net.InetAddresses.isInetAddress;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class IPCookieUpdater {
    private static final Logger log = LoggerFactory.getLogger(IPCookieUpdater.class);
    public Cookie updateCookie( Cookie cookie, HttpServletRequest request) {
        String referer = request.getHeader("referer");
        if (isBlank(referer)) {
            return cookie;
        }

        try {
            URL url = URI.create( referer ).toURL();
            String host = url.getHost();
            if (isInetAddress(host) || "localhost".equals(host)) {
                cookie.setDomain(host);
                return cookie;
            }

        } catch (Exception e) {
            log.error("Cannot update cookie to referer[{}].", referer);
        }
        return cookie;
    }
}
