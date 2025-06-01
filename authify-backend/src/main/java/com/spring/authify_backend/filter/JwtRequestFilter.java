package com.spring.authify_backend.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.spring.authify_backend.service.auth.AppUserDetailsService;
import com.spring.authify_backend.utils.JwtUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

	private final AppUserDetailsService userDetailsService;
	private final JwtUtils jwtUtils;

	private static final List<String> PUBLIC_URLS = List.of("/login", "/register", "send-reset-otp", "/reset-password",
			"/logout");

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String path = request.getServletPath();
		
		if (PUBLIC_URLS.contains(path)) {
			filterChain.doFilter(request, response);
			return;
		}

		String jwt = null;
		String email = null;

		// check autorized headers
		final String autherizedHeader = request.getHeader("Authorization");
		if (autherizedHeader != null && autherizedHeader.startsWith("Bearer ")) {
			jwt = autherizedHeader.substring(7);
		}

		// if not found in the header, check cookie
		if (jwt == null) {
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if ("jwt".equals(cookie.getName())) {
						jwt = cookie.getValue();
						break;
					}
				}
			}
		}

		// validate the token and set security context
		if (jwt != null) {
			email = jwtUtils.extractEmail(jwt);
			
			if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(email);
				
				if (jwtUtils.validateToken(jwt, userDetails)) {
					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				}
			}
		}
		
		filterChain.doFilter(request, response);
	}
}
