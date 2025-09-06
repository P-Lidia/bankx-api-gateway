package com.itgirls.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

public class JwtHeaderFilter extends AbstractGatewayFilterFactory<Object> {

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) ->
                ReactiveSecurityContextHolder.getContext()
                        .flatMap(securityContext -> {
                            Authentication authentication = securityContext.getAuthentication();
                            if (authentication == null || !authentication.isAuthenticated()) {
                                return chain.filter(exchange);
                            }
                            if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
                                return chain.filter(exchange);
                            }
                            String userId = jwtAuth.getToken().getClaimAsString("userId");
                            return Mono.justOrEmpty(authentication.getAuthorities().stream().findFirst())
                                    .map(Object::toString)
                                    .switchIfEmpty(Mono.error(new ResponseStatusException(
                                            HttpStatus.FORBIDDEN, "User role not found"
                                    )))
                                    // Добавляем заголовки
                                    .flatMap(role -> {
                                        exchange.getRequest().mutate()
                                                .header("X-User-Id", userId)
                                                .header("X-User-Role", role)
                                                .build();
                                        return chain.filter(exchange);
                                    });
                        })
                        .switchIfEmpty(chain.filter(exchange));
    }
}