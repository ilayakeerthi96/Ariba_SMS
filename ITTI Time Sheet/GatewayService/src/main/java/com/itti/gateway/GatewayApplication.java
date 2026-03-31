// package com.itti.gateway;

// import java.util.function.Function;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.actuate.autoconfigure.security.reactive.ReactiveManagementWebSecurityAutoConfiguration;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
// import org.springframework.cloud.gateway.filter.factory.DedupeResponseHeaderGatewayFilterFactory;
// import org.springframework.cloud.gateway.route.RouteLocator;
// import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
// import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
// import org.springframework.cloud.gateway.route.builder.UriSpec;
// import org.springframework.context.annotation.Bean;
// import org.springframework.http.HttpHeaders;
// import com.itti.gateway.config.AuthenticationFilterGateway;
// import com.itti.gateway.util.Constants;

// @SpringBootApplication(exclude = { ReactiveSecurityAutoConfiguration.class,
// 		ReactiveManagementWebSecurityAutoConfiguration.class
// })
// public class GatewayApplication {

// 	@Autowired
// 	private AuthenticationFilterGateway authenticationFilter;

// 	public static void main(String[] args) {
// 		SpringApplication.run(GatewayApplication.class, args);
// 	}

// 	@Bean
// 	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

// 		return builder.routes()
// 				.route("timesheet", r -> r.path("/app/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("purchaseorder", r -> r.path("/purchaseorder/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("role", r -> r.path("/role/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("userrole", r -> r.path("/userrole/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("timesheet", r -> r.path("/customerPaymentTerm/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("timesheet", r -> r.path("/paymentTerm/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("timesheet", r -> r.path("/practiceMaster/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("download", r -> r.path("/download/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("timesheet", r -> r.path("/timesheet/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("project", r -> r.path("/project/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("projectactivity", r -> r.path("/projectactivity/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("report", r -> r.path("/report/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("billingindent", r -> r.path("/billingindent/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("billingindent", r -> r.path("/billingindentdetails/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("billingindent", r -> r.path("/customer/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("billingindent", r -> r.path("/customers/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("employee", r -> r.path("/employee/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("holiday", r -> r.path("/holiday/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("contacts", r -> r.path("/api/contacts/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("vendors", r -> r.path("/api/vendors/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("indents", r -> r.path("/api/indents/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))

// 				.route("item_master", r -> r.path("/api/item_master/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))

// 				.route("item_master_lookup", r -> r.path("/api/item_master_lookup/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("po", r -> r.path("/api/po/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))

// 				.route("super po", r -> r.path("/super-po/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("super po", r -> r.path("/api/super-po-splits/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("super po", r -> r.path("/import/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("super po", r -> r.path("/progress/subscribe/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("super po", r -> r.path("/invoices/upload/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.TIMESHEET_HOSTS))
// 				.route("leadcapture", r -> r.path("/leadcapture/**")
// 						.filters(dedupeResponseHeaders())
// 						.uri(Constants.LC_HOSTS))
// 				.build();
// 	}

// 	private Function<GatewayFilterSpec, UriSpec> dedupeResponseHeaders() {
// 		String strategy = DedupeResponseHeaderGatewayFilterFactory.Strategy.RETAIN_FIRST.name();
// 		return f -> f.dedupeResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, strategy)
// 				.dedupeResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, strategy)
// 				.addRequestHeader("x-user", "test")
// 				.filter(authenticationFilter);
// 	}
// }


package com.itti.gateway;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.reactive.ReactiveManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.cloud.gateway.filter.factory.DedupeResponseHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.UriSpec;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import com.itti.gateway.config.AuthenticationFilterGateway;
import com.itti.gateway.util.Constants;

@SpringBootApplication(exclude = { 
    ReactiveSecurityAutoConfiguration.class,
    ReactiveManagementWebSecurityAutoConfiguration.class
})
public class GatewayApplication {

    @Autowired
    private AuthenticationFilterGateway authenticationFilter;

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        return builder.routes()
                // ============================================
                // ✅ LEAD CAPTURE ROUTES (Buyer Service)
                // ============================================
                .route("leadcapture", r -> r.path("/leadcapture/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.LC_HOSTS))

                // ============================================
                // TIMESHEET ROUTES
                // ============================================
                .route("timesheet", r -> r.path("/app/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("purchaseorder", r -> r.path("/purchaseorder/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("role", r -> r.path("/role/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("userrole", r -> r.path("/userrole/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("customerPaymentTerm", r -> r.path("/customerPaymentTerm/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("paymentTerm", r -> r.path("/paymentTerm/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("practiceMaster", r -> r.path("/practiceMaster/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("download", r -> r.path("/download/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("timesheet-details", r -> r.path("/timesheet/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("project", r -> r.path("/project/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("projectactivity", r -> r.path("/projectactivity/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("report", r -> r.path("/report/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("billingindent", r -> r.path("/billingindent/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("billingindentdetails", r -> r.path("/billingindentdetails/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("customer", r -> r.path("/customer/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("customers", r -> r.path("/customers/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("employee", r -> r.path("/employee/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("holiday", r -> r.path("/holiday/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("contacts", r -> r.path("/api/contacts/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("vendors", r -> r.path("/api/vendors/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("indents", r -> r.path("/api/indents/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("item_master", r -> r.path("/api/item_master/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("item_master_lookup", r -> r.path("/api/item_master_lookup/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("po", r -> r.path("/api/po/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("super-po", r -> r.path("/super-po/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("super-po-splits", r -> r.path("/api/super-po-splits/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("import", r -> r.path("/import/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("progress-subscribe", r -> r.path("/progress/subscribe/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                .route("invoices-upload", r -> r.path("/invoices/upload/**")
                        .filters(dedupeResponseHeaders())
                        .uri(Constants.TIMESHEET_HOSTS))
                
                .build();
    }

    private Function<GatewayFilterSpec, UriSpec> dedupeResponseHeaders() {
        String strategy = DedupeResponseHeaderGatewayFilterFactory.Strategy.RETAIN_FIRST.name();
        return f -> f
                .dedupeResponseHeader("Access-Control-Allow-Origin", strategy)
                .dedupeResponseHeader("Access-Control-Allow-Credentials", strategy)
                .addRequestHeader("x-user", "test")
                .filter(authenticationFilter);
    }
}