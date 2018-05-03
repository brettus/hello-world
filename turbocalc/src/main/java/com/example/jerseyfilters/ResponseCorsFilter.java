package com.example.jerseyfilters;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class ResponseCorsFilter implements ContainerResponseFilter {

	final Set<String> acceptedOrigins = new TreeSet<>();
	{
		acceptedOrigins.add("http://turbocalendulator-services.herokuapp.com");
		acceptedOrigins.add("https://turbocalendulator-services.herokuapp.com");
		acceptedOrigins.add("http://turbocalendulator-web.herokuapp.com");
		acceptedOrigins.add("https://turbocalendulator-web.herokuapp.com");
		acceptedOrigins.add("http://localhost:5000");
		acceptedOrigins.add("http://localhost:5001");
		acceptedOrigins.add("http://beta:5000");
		acceptedOrigins.add("http://127.0.0.1:5000");
		// acceptedOrigins.add("http://fast-anchorage-1433.herokuapp.com");
	}

	final Set<String> acceptedHeaders = new TreeSet<>();
	{
		acceptedHeaders.add("Accept");
		acceptedHeaders.add("Accept-Encoding");
		acceptedHeaders.add("Accept-Language");
		acceptedHeaders.add("Cache-Control");
		acceptedHeaders.add("Connection");
		acceptedHeaders.add("Content-Length");
		acceptedHeaders.add("Content-Type");
		acceptedHeaders.add("Host");
		acceptedHeaders.add("Origin");
		acceptedHeaders.add("Pragma");
		acceptedHeaders.add("Referer");
		acceptedHeaders.add("User-Agent");
	}

	final Set<String> acceptedMethods = new TreeSet<>();
	{
		acceptedMethods.add("POST");
		acceptedMethods.add("GET");
		acceptedMethods.add("PUT");
		acceptedMethods.add("DELETE");
		acceptedMethods.add("OPTIONS");
	}

	public String join(final Collection<String> strings, final String separator) {
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (final String item : strings) {
			if (!first) {
				sb.append(separator);
			}
			sb.append(item);
			first = false;
		}
		return sb.toString();
	}

	@Override
	public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
			throws IOException {

		final String origin = requestContext.getHeaderString("Origin");
		if(null == origin) {
			// not a cross-domain request
			return;
		}

		if(acceptedOrigins.contains(origin)) {
			responseContext.getHeaders().putSingle("Access-Control-Allow-Origin", origin);
			responseContext.getHeaders().putSingle("Access-Control-Allow-Headers", join(acceptedHeaders, ", "));
			responseContext.getHeaders().putSingle("Access-Control-Allow-Methods", join(acceptedMethods, ", "));
		}
	}

}
