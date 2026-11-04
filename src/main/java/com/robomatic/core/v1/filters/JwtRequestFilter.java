package com.robomatic.core.v1.filters;

import com.robomatic.core.v1.entities.UserEntity;
import com.robomatic.core.v1.exceptions.NotFoundException;
import com.robomatic.core.v1.models.UserModel;
import com.robomatic.core.v1.repositories.UserRepository;
import com.robomatic.core.v1.utils.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.robomatic.core.v1.exceptions.messages.NotFoundErrorCode.E404008;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtRequestFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Resource(name = "requestScopeRequestData")
    private UserModel user;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/core/v1/auth/")) {
            chain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                username = null;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserEntity userEntity = userRepository.findByEmail(username)
                    .orElseThrow(() -> new NotFoundException(E404008));
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    userEntity.getEmail(),
                    "$2a$10$Bk16tW8XzkR8FzEc4FY6We.DJhBRyHlBccJv.s04FgBL.hJmL8qXG",
                    new java.util.ArrayList<>()
            );
            try {
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    populateUser(userEntity);
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            } catch (Exception ignored) {
            }
        }
        chain.doFilter(request, response);
    }

    private void populateUser(UserEntity userEntity) {
        user.setEmail(userEntity.getEmail());
        user.setFullName(userEntity.getFullName());
        user.setId(userEntity.getId());
        user.setPhone(userEntity.getPhone());
        user.setRoleId(userEntity.getRoleId());
        user.setEnabled(userEntity.isEnabled());
    }

}
