package example.nosql;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UrlRedirectFilter implements Filter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		String uri = req.getRequestURI();
		String getProtocol = req.getScheme();
		String getDomain = req.getServerName();
		String getQParameter = req.getQueryString();

		if (getProtocol.toLowerCase().equals("http")) {
			// Set response content type
			response.setContentType("text/html");
			String httpsPath = "https" + "://" + getDomain + uri;

			if (getQParameter != null) {
				httpsPath += "?" + getQParameter;
			}

			String site = new String(httpsPath);
			resp.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
			resp.setHeader("Location", site);
		}

		// Pass request back down the filter chain
		chain.doFilter(req, resp);
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
}
