// package com.itti.gateway.config;

// import org.springframework.cloud.context.config.annotation.RefreshScope;
// import org.springframework.cloud.gateway.filter.GatewayFilter;
// import org.springframework.cloud.gateway.filter.GatewayFilterChain;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.server.reactive.ServerHttpRequest;
// import org.springframework.http.server.reactive.ServerHttpResponse;
// import org.springframework.stereotype.Component;
// import org.springframework.web.server.ServerWebExchange;

// import com.itti.gateway.service.JwtService;

// import reactor.core.publisher.Mono;

// @RefreshScope
// @Component
// public class AuthenticationFilterGateway implements GatewayFilter {

//     private final RouterValidator routerValidator;
//     private final JwtService jwtService;

//     public AuthenticationFilterGateway(RouterValidator routerValidator, JwtService jwtService) {
//         this.routerValidator = routerValidator;
//         this.jwtService = jwtService;
//     }

//     @Override
//     public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

//         ServerHttpRequest request = exchange.getRequest();

//         if (routerValidator.isSecured.test(request)) {
//             if (this.isAuthMissing(request)) {
//                 return this.onError(exchange, HttpStatus.UNAUTHORIZED);
//             }

//             final String token = this.getAuthHeader(request);

//             String jwt = token.split(" ")[1].trim();

//             String username = jwtService.extractUserName(jwt.trim());
//             if (username ==null) {
//                 return this.onError(exchange, HttpStatus.FORBIDDEN);
//             }
//             ServerHttpRequest serverHttpRequest = this.updateRequest(exchange, jwt);
//             return chain.filter(exchange.mutate().request(serverHttpRequest).build());      
//         }
//         return chain.filter(exchange);
//     }

//     private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
//         ServerHttpResponse response = exchange.getResponse();
//         response.setStatusCode(httpStatus);
//         return response.setComplete();
//     }

//     private String getAuthHeader(ServerHttpRequest request) {
//         return request.getHeaders().getOrEmpty("Authorization").get(0);
//     }

//     private boolean isAuthMissing(ServerHttpRequest request) {
//         return !request.getHeaders().containsKey("Authorization");
//     }

//     private ServerHttpRequest updateRequest(ServerWebExchange exchange, String token) {
//         String empNo = jwtService.extractUserName(token);
//         return exchange.getRequest().mutate()
//                 .header("X-client-name", empNo)
//                 .build();
//     }

// }


// package com.itti.gateway.config;

// import org.springframework.cloud.context.config.annotation.RefreshScope;
// import org.springframework.cloud.gateway.filter.GatewayFilter;
// import org.springframework.cloud.gateway.filter.GatewayFilterChain;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.server.reactive.ServerHttpRequest;
// import org.springframework.http.server.reactive.ServerHttpResponse;
// import org.springframework.stereotype.Component;
// import org.springframework.web.server.ServerWebExchange;

// import com.itti.gateway.service.JwtService;

// import reactor.core.publisher.Mono;

// @RefreshScope
// @Component
// public class AuthenticationFilterGateway implements GatewayFilter {

//     private final RouterValidator routerValidator;
//     private final JwtService jwtService;

//     public AuthenticationFilterGateway(RouterValidator routerValidator, JwtService jwtService) {
//         this.routerValidator = routerValidator;
//         this.jwtService = jwtService;
//     }

//     @Override
//     public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

//         ServerHttpRequest request = exchange.getRequest();
//         String path = request.getURI().getPath();

//         System.out.println("🔵 GATEWAY FILTER: " + request.getMethod() + " " + path);

//         // ✅ Check if endpoint is secured
//         if (routerValidator.isSecured.test(request)) {
//             System.out.println("🔒 SECURED ENDPOINT - Checking Authorization");
            
//             if (this.isAuthMissing(request)) {
//                 System.out.println("❌ Authorization header missing");
//                 return this.onError(exchange, HttpStatus.UNAUTHORIZED);
//             }

//             final String token = this.getAuthHeader(request);

//             String jwt = token.split(" ")[1].trim();

//             String username = jwtService.extractUserName(jwt.trim());
//             if (username == null) {
//                 System.out.println("❌ Invalid token");
//                 return this.onError(exchange, HttpStatus.FORBIDDEN);
//             }
            
//             System.out.println("✅ Token valid for user: " + username);
//             ServerHttpRequest serverHttpRequest = this.updateRequest(exchange, jwt);
//             return chain.filter(exchange.mutate().request(serverHttpRequest).build());
//         }
        
//         // ✅ Open endpoint - allow through
//         System.out.println("✅ OPEN ENDPOINT - Allowing through");
//         return chain.filter(exchange);
//     }

//     private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
//         ServerHttpResponse response = exchange.getResponse();
//         response.setStatusCode(httpStatus);
//         return response.setComplete();
//     }

//     private String getAuthHeader(ServerHttpRequest request) {
//         return request.getHeaders().getOrEmpty("Authorization").get(0);
//     }

//     private boolean isAuthMissing(ServerHttpRequest request) {
//         return !request.getHeaders().containsKey("Authorization");
//     }

//     private ServerHttpRequest updateRequest(ServerWebExchange exchange, String token) {
//         String empNo = jwtService.extractUserName(token);
//         return exchange.getRequest().mutate()
//                 .header("X-client-name", empNo)
//                 .build();
//     }
// }



package com.itti.gateway.config;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.itti.gateway.service.JwtService;

import reactor.core.publisher.Mono;

@RefreshScope
@Component
public class AuthenticationFilterGateway implements GatewayFilter {

    private final RouterValidator routerValidator;
    private final JwtService jwtService;

    public AuthenticationFilterGateway(RouterValidator routerValidator,
                                       JwtService jwtService) {
        this.routerValidator = routerValidator;
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        if (routerValidator.isSecured.test(request)) {

            if (!request.getHeaders().containsKey("Authorization")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = request.getHeaders()
                    .getFirst("Authorization")
                    .replace("Bearer ", "");

            String username = jwtService.extractUserName(token);

            if (username == null) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            ServerHttpRequest mutatedRequest = exchange.getRequest()
                    .mutate()
                    .header("X-USER", username)
                    .build();

            return chain.filter(exchange.mutate()
                    .request(mutatedRequest)
                    .build());
        }

        return chain.filter(exchange);
    }
}
