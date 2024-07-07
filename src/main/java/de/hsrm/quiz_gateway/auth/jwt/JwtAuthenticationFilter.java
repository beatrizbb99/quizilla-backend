package de.hsrm.quiz_gateway.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final List<String> UNAUTHENTICATED_ENDPOINTS = Arrays.asList("/api/login", "/api/register","/oauth/**", "/hello");

    @Autowired
    UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(!UNAUTHENTICATED_ENDPOINTS.contains(request.getServletPath())) {
            try {
                String authorization = request.getHeader("Authorization");
                if(authorization == null)
                    throw new RuntimeException("Missing header");
                String[] spl = authorization.split(" ", 2);
                if(spl.length != 2 || !spl[0].equals("Bearer")) {
                    throw new RuntimeException("Invalid header value");
                }
                DecodedJWT decoded = JWT.require(Algorithm.HMAC256("1234")).build().verify(spl[1]);
                String sub = decoded.getSubject();
                UserDetails userDetails = userDetailsService.loadUserByUsername(sub);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (UsernameNotFoundException ex) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}

