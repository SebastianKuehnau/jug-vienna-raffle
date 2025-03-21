package com.vaadin.demo.application.security.conf;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.HandlerHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.log.LogMessage;
import org.springframework.security.web.DefaultRedirectStrategy;

import java.io.IOException;

/**
 * This class is a copy of the {@link DefaultRedirectStrategy}
 * <p>
 * By default, the redirection is done in an AJAX but it's blocked by the OAUTH2 server.
 * If the log out is not working you can open the Network tab in the Inspector Browser Tools and check the "Preserve log"
 * You can see the logout URL
 */
public class VaadinRedirectStrategy extends DefaultRedirectStrategy {
    /**
     * Redirects the response to the supplied URL.
     * <p>
     * If <tt>contextRelative</tt> is set, the redirect value will be the value after the
     * request context path. Note that this will result in the loss of protocol
     * information (HTTP or HTTPS), so will cause problems if a redirect is being
     * performed to change to HTTPS, for example.
     * <p>
     * It's mostly a copy of the DefaultRedirectStrategy constructor.
     */
    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        String redirectUrl = calculateRedirectUrl(request.getContextPath(), url);
        redirectUrl = response.encodeRedirectURL(redirectUrl);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(LogMessage.format("Redirecting to %s", redirectUrl));
        }
        final var servletMapping = request.getHttpServletMapping().getPattern();
        // [CHANGE] If the request is a Vaadin internal request then change the location of the page
        if (HandlerHelper.isFrameworkInternalRequest(servletMapping, request)) {
            // Avoid CORS error, don't use XHR request
            UI.getCurrent().getPage().setLocation(redirectUrl);
        } else {
            // AJAX redirection
            this.logger.warn(LogMessage.format("Might be an issue - Redirecting to %s", redirectUrl));
            response.sendRedirect(redirectUrl);
        }
    }


}