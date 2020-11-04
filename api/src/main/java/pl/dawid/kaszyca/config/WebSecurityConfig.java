package pl.dawid.kaszyca.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;
import pl.dawid.kaszyca.security.jwt.JWTConfigurer;
import pl.dawid.kaszyca.security.jwt.TokenProvider;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Import(SecurityProblemSupport.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;

    public WebSecurityConfig(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/messages").hasAuthority(AuthoritiesConstants.USER)
                .antMatchers( "/api/account/profile").hasAuthority(AuthoritiesConstants.USER)
                .antMatchers( "/api/messages").hasAuthority(AuthoritiesConstants.USER)
                .antMatchers(HttpMethod.POST, "/api/auctions").hasAuthority(AuthoritiesConstants.USER)
                .antMatchers(HttpMethod.PUT, "/api/auctions").hasAuthority(AuthoritiesConstants.USER)
                .antMatchers(HttpMethod.DELETE, "/api/auctions").hasAuthority(AuthoritiesConstants.USER)
                .antMatchers(HttpMethod.POST, "/api/attachments").hasAuthority(AuthoritiesConstants.USER)
                .antMatchers(HttpMethod.PUT, "/api/attachments").hasAuthority(AuthoritiesConstants.USER)
                .antMatchers(HttpMethod.PUT, "/api/account/password").hasAuthority(AuthoritiesConstants.USER)
                .antMatchers(HttpMethod.PUT, "/api/auctions/edit").hasAuthority(AuthoritiesConstants.USER)
                .antMatchers(HttpMethod.POST, "/api/categories").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers(HttpMethod.GET, "/api/report/auctions").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers(HttpMethod.PUT, "/api/report/auctions").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/api/**").permitAll()
                .and()
                .httpBasic()
                .and()
                .apply(securityConfigurerAdapter());
    }

    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider);
    }

}
