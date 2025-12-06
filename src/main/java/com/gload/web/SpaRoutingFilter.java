package com.gload.web;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
@PreMatching
public class SpaRoutingFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        UriInfo uriInfo = requestContext.getUriInfo();
        String path = uriInfo.getPath();

        if (!path.startsWith("api/") &&
            !path.contains(".") &&
            !path.equals("") &&
            requestContext.getMethod().equals("GET")) {
            requestContext.setRequestUri(
                uriInfo.getBaseUri().resolve("/")
            );
        }
    }
}
