package com.xebia;

import com.xebia.filter.AuthenticationRequestFilter;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.springframework.stereotype.Component;

import javax.ws.rs.container.ContainerRequestFilter;

public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
        register(RequestContextFilter.class);
        register(ContainerRequestFilter.class);
        packages("com.xebia");
        register(AuthenticationRequestFilter.class);
        register(LoggingFilter.class);
    }
}