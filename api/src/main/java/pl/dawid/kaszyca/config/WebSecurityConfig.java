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
                .antMatchers("/api/messages").hasAnyAuthority(AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN)
                .antMatchers("/api/account/profile").hasAnyAuthority(AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN)
                .antMatchers("/api/messages").hasAnyAuthority(AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN)
                .antMatchers(HttpMethod.POST, "/api/auctions").hasAnyAuthority(AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN)
                .antMatchers(HttpMethod.PUT, "/api/auctions").hasAnyAuthority(AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN)
                .antMatchers(HttpMethod.DELETE, "/api/auctions").hasAnyAuthority(AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN)
                .antMatchers(HttpMethod.POST, "/api/attachments").hasAnyAuthority(AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN)
                .antMatchers(HttpMethod.PUT, "/api/attachments").hasAnyAuthority(AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN)
                .antMatchers(HttpMethod.PUT, "/api/account/password").hasAnyAuthority(AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN)
                .antMatchers(HttpMethod.PUT, "/api/auctions/edit").hasAnyAuthority(AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN)
                .antMatchers(HttpMethod.POST, "/api/categories").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers(HttpMethod.GET, "/api/report/auctions").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers(HttpMethod.PUT, "/api/report/auctions").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers(HttpMethod.GET, "/api/account/user-list").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers(HttpMethod.PUT, "/api/account/grant/users/").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers(HttpMethod.DELETE, "/api/account/users").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers(HttpMethod.GET, "api/statistics/admin").hasAuthority(AuthoritiesConstants.ADMIN)
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
