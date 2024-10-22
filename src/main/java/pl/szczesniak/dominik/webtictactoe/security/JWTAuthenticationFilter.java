package pl.szczesniak.dominik.webtictactoe.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JWTGenerator tokenGenerator;
	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
									final FilterChain filterChain) throws ServletException, IOException {
		final String token = getJWTFromRequest(request);
		if (StringUtils.hasText(token) && tokenGenerator.validateToken(token)) {
			final String username = tokenGenerator.getUsernameFromJWT(token);
			final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
					userDetails, null, userDetails.getAuthorities());
			authenticationToken.setDetails(userDetails);
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		}
		filterChain.doFilter(request, response);
	}

	private String getJWTFromRequest(final HttpServletRequest request) {
		final String token = request.getHeader("Authorization");
		if (StringUtils.hasText(token)) {
			return token;
		}
		return null;
	}

}
