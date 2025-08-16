package org.example.dormallocationsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class AuthenticationSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
       return httpSecurity.authorizeHttpRequests((requests) -> {
            requests.requestMatchers("/", "/login", "/register", "/employee/register").permitAll()
                    .requestMatchers("/employee/**").hasRole("EMPLOYEE")
                    .requestMatchers("/dashboard/**", "/upload-documents/**", "/choose-room/**", "/apply-room/**").hasRole("STUDENT")
                    .anyRequest().authenticated();
        }).formLogin((form) -> form.loginPage("/login")
               .loginProcessingUrl("/do-login")
               .usernameParameter("email")
                       .passwordParameter("password")
                       .defaultSuccessUrl("/post-login", true).
               permitAll()).logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
        )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
