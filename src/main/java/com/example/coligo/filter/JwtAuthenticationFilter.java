package com.example.coligo.filter;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import com.example.coligo.service.JwtService;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request, 
        @NonNull HttpServletResponse response, 
        @NonNull FilterChain filterChain ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");//header
        final String jwt; //token
        final String userEmail;

        //verifier si notre header est null ou bien ce n'est pas un 'Bearer'
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt) ; // extract the user email from Jwt token
        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null ){ //utilisateur pas encore authentifie
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail); //recuperer le user
            if (jwtService.isTokenValid(jwt, userDetails)) { //si le token est valide 
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request,response);
    
    
    }
    
}
