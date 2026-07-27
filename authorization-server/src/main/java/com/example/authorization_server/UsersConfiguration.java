package com.example.authorization_server;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.List;

@Configuration
class UsersConfiguration {

    @Bean
    ApplicationRunner runner(PasswordEncoder passwordEncoder) {
        return a -> IO.println(passwordEncoder.encode("spring"));
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    InMemoryUserDetailsManager userDetailsManager(PasswordEncoder passwordEncoder) {
        var josh = User //
                .builder()//
                .username("josh@joshlong.com") //
                .roles("USER")//
                .password(passwordEncoder.encode("pw"))//
                .build();
        var rob = User //
                .builder()//
                .username("rob@spring.security") //
                .roles("USER", "ADMIN")//
                .password(passwordEncoder.encode("pw"))//
                .build();
        var userDetails = List.of(rob, josh);
        return new InMemoryUserDetailsManager(userDetails);
    }
}
