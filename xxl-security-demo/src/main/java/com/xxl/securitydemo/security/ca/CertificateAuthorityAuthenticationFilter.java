package com.xxl.securitydemo.security.ca;

import com.xxl.securitydemo.security.ca.CertificateAuthorityAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CertificateAuthorityAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    public static final String SPRING_SECURITY_FORM_SIGNATURE_KEY = "signature";

    private String signatureParameter = SPRING_SECURITY_FORM_SIGNATURE_KEY;
    private boolean postOnly = true;

    public CertificateAuthorityAuthenticationFilter() {
        super(new AntPathRequestMatcher("/certificate_authority_login", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        String signature = obtainSignature(request);

        if (signature == null) {
            signature = "";
        }

        signature = signature.trim();

        CertificateAuthorityAuthenticationToken authRequest = new CertificateAuthorityAuthenticationToken(signature);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected String obtainSignature(HttpServletRequest request) {
        return request.getParameter(signatureParameter);
    }

    /**
     * Provided so that subclasses may configure what is put into the authentication
     * request's details property.
     */
    protected void setDetails(HttpServletRequest request,
                              CertificateAuthorityAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }
}
