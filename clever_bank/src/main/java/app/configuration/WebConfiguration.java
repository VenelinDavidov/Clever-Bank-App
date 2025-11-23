package app.configuration;



import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableMethodSecurity
public class WebConfiguration implements WebMvcConfigurer {



    // Spring Security implementation
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .authorizeHttpRequests (matcher -> matcher
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers ("/", "/register", "/messages", "/about", "/thank-you").permitAll ()
                        .anyRequest ().authenticated ())
                .formLogin (form -> form
                        .loginPage ("/login")
                        .defaultSuccessUrl ("/home", true)
                        .failureHandler(authenticationFailureHandler())
                        .permitAll ())
                .logout (logout -> logout
                        .logoutRequestMatcher (new AntPathRequestMatcher ("/logout", "GET"))
                        .logoutSuccessUrl ("/"));

        return httpSecurity.build ();
    }


              // AuthenticationFailureHandler implementation for password and account
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return(request, response, exception) -> {
            String errorMessage = "Invalid username or password";

            if (exception instanceof org.springframework.security.authentication.AccountExpiredException) {
                errorMessage = "Your account has expired. Please contact support.";
            } else if (exception instanceof org.springframework.security.authentication.CredentialsExpiredException) {
                errorMessage = "Your password has expired. Please reset your password.";
            } else if (exception instanceof org.springframework.security.authentication.LockedException) {
                errorMessage = "Your account is locked.";
            } else if (exception instanceof org.springframework.security.authentication.DisabledException) {
                errorMessage = "Your account is disabled.";
            }

                request.getSession ().setAttribute ("error", errorMessage);
                response.sendRedirect("/login?error");
        };
    }

}