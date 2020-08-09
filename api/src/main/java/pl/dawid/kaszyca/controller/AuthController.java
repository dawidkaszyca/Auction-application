package pl.dawid.kaszyca.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.dawid.kaszyca.security.jwt.JWTFilter;
import pl.dawid.kaszyca.security.jwt.TokenProvider;
import pl.dawid.kaszyca.service.StatisticService;
import pl.dawid.kaszyca.vm.LoginFormVM;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("api")
public class AuthController {

    private TokenProvider tokenProvider;
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    private StatisticService statisticService;

    public AuthController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder,
                          StatisticService statisticService) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.statisticService = statisticService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity authorize(@Valid @RequestBody LoginFormVM loginVM) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Boolean rememberMe = (loginVM.getRememberMe() == null) ? false : loginVM.getRememberMe();
            String jwt = tokenProvider.createToken(authentication, rememberMe);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
            statisticService.incrementDailyLogin();
            return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
        } catch (BadCredentialsException | InternalAuthenticationServiceException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.valueOf(401));
        }
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}
