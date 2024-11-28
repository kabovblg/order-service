//package com.blagovestkabov.order_service.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//import org.springframework.security.config.Customizer;
//
//
//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class WebSecurityConfig {
//    @Bean
//    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity serverHttpSecurity) {
//        serverHttpSecurity
//                .authorizeExchange(authorizeExchangeSpec ->  authorizeExchangeSpec.anyExchange().authenticated())
//                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
//                .oauth2Login(Customizer.withDefaults());
//        return serverHttpSecurity.build();
//    }
//}
