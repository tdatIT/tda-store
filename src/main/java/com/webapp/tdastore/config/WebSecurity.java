package com.webapp.tdastore.config;

import com.webapp.tdastore.services.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomUserDetailService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().loginPage("/dang-nhap")
                .defaultSuccessUrl("/trang-chu")
                .usernameParameter("email")
                .passwordParameter("password")
                .failureUrl("/dang-nhap?error=true")
                .and().logout().logoutSuccessUrl("/trang-chu")
                .and().csrf().disable();
        http.rememberMe().key("uniqueAndSecret").tokenValiditySeconds(60 * 24 * 3);

        //Authentication and authorize
        http.authorizeRequests().antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')");
    }

}
