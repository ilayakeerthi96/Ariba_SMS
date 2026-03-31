// package com.itti.gateway;

// import java.util.Arrays;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.cors.reactive.CorsWebFilter;
// import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

// import com.itti.gateway.util.Constants;

// @Configuration
// public class CorsConfiguration extends org.springframework.web.cors.CorsConfiguration {

//   @Bean
//   public CorsWebFilter corsFilter() {
//     org.springframework.web.cors.CorsConfiguration corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
//     corsConfiguration.setAllowCredentials(true);
//     corsConfiguration.addAllowedOrigin(Constants.ALLOWED_HOSTS);
//     corsConfiguration.addAllowedOrigin(Constants.ALLOWED_HOSTS_PROD);
//     corsConfiguration.addAllowedOrigin(Constants.ALLOWED_HOSTS_PROD_1);
//     corsConfiguration.addAllowedOrigin(Constants.ALLOWED_HOSTS_PROD_PUBLIC);
//     corsConfiguration.addAllowedOrigin(Constants.ALLOWED_HOSTS_LOCAL);
//     corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
//     corsConfiguration.addAllowedHeader("origin");
//     corsConfiguration.addAllowedHeader("content-type");
//     corsConfiguration.addAllowedHeader("accept");
//     corsConfiguration.addAllowedHeader("authorization");
//     corsConfiguration.addAllowedHeader("X-client-name");
//     corsConfiguration.addAllowedHeader("cookie");
//     corsConfiguration.addAllowedHeader("token");
//     corsConfiguration.addAllowedHeader("role");
//     corsConfiguration.addAllowedHeader("empid");

//     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//     source.registerCorsConfiguration("/**", corsConfiguration);
//     return new CorsWebFilter(source);
//   }

// }


package com.itti.gateway;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.itti.gateway.util.Constants;

@Configuration
public class CorsConfiguration extends org.springframework.web.cors.CorsConfiguration {

    @Bean
    public CorsWebFilter corsFilter() {
        org.springframework.web.cors.CorsConfiguration corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        
        // ============================================
        // ✅ Add all allowed origins
        // ============================================
        corsConfiguration.addAllowedOrigin(Constants.ALLOWED_HOSTS);
        corsConfiguration.addAllowedOrigin(Constants.ALLOWED_HOSTS_PROD);
        corsConfiguration.addAllowedOrigin(Constants.ALLOWED_HOSTS_PROD_1);
        corsConfiguration.addAllowedOrigin(Constants.ALLOWED_HOSTS_PROD_PUBLIC);
        corsConfiguration.addAllowedOrigin(Constants.ALLOWED_HOSTS_LOCAL);
        
        // ============================================
        // ✅ Allow all HTTP methods
        // ============================================
        corsConfiguration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"
        ));
        
        // ============================================
        // ✅ Allow all headers
        // ============================================
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedHeader("origin");
        corsConfiguration.addAllowedHeader("content-type");
        corsConfiguration.addAllowedHeader("accept");
        corsConfiguration.addAllowedHeader("authorization");
        corsConfiguration.addAllowedHeader("X-client-name");
        corsConfiguration.addAllowedHeader("cookie");
        corsConfiguration.addAllowedHeader("token");
        corsConfiguration.addAllowedHeader("role");
        corsConfiguration.addAllowedHeader("empid");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(source);
    }
}