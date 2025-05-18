package com.example.coligo.service;


import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


@Service
public class JwtService {


    private static final String SECRET_KEY = "uP1zx2m6qcMxyqoo9kJqaz7ei+EJqlYzmY5H1FVBkql/j8W5e2ZorL+UlDDp2xaFbaqNHywY/f1OaNPhbQkDUnANQZK3bMGU7GE9/Nj+vJOCBeywCL92yO+1CrN/I/eE5Kg/BCI3sUrFZ5dKYOLJOOXYIBr4s8w5ZVFD2Ui47fgco0lFUIVhs5oICfgZp38kWrWacPySslJec6Qt/SqmaNnrioXGv0RcRoKfjXrexRJ7Hum/kfxtRCwM38jV1h8mMTC7DOCFQ1KXpjUTgYbkpW2S/QQbHwzoGSdh5GNhflPrEjWsw0P7laXs2JprEFMSbEFjO7BJDlYJXseVtl/64XVInEfX2FGqBwn9Ju0H9KA=";
    private static final long TOKEN_VALIDITY = 1000 * 60 * 60 * 24; // 24 heures



    public String extractUsername(String token) { //extraire le username
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); //EXTRAIRE TOUTES LES CLAIMS
        return claimsResolver.apply(claims);
    }
    
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails);
    }

    //methode pour tester si le token est expire, extraire la date d'expiration, generer le token ,..
   //generate a token out of extraClaims et userDetails 
    public String generateToken(Map<String,Object> extraClaims, UserDetails userDetails){
        return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))//calculer la date d'expiration
            .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
            .signWith(getSignInkey(), SignatureAlgorithm.HS256)
            .compact();
    }


    //methode pour valider un token 
    //verifier si le token correspond a l'utilisateur
    public Boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token); //extraire le username (dans notre cas le email) du token
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }


    private Boolean isTokenExpired(String token){
            return extractExpiration(token).before(new Date());
    }
            
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
            
                private Claims extractAllClaims (String token){
        return Jwts
        .parserBuilder()
        .setSigningKey(getSignInkey())
                .build()
                .parseClaimsJws(token)
                .getBody();
            }
        
    private Key getSignInkey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
        
    }

    
    
}