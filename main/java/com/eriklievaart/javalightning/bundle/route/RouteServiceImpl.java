package com.eriklievaart.javalightning.bundle.route;

import java.util.Map;

import com.eriklievaart.javalightning.bundle.api.RequestContext;
import com.eriklievaart.javalightning.bundle.api.exception.NotFound404Exception;
import com.eriklievaart.javalightning.bundle.api.page.Route;
import com.eriklievaart.javalightning.bundle.api.page.RouteService;
import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.lang.api.str.Str;

public class RouteServiceImpl implements RouteService {

	private PageServiceIndex routes;

	public RouteServiceImpl(PageServiceIndex routes) {
		this.routes = routes;
	}

	@Override
	public Route getRoute(String service, String route) throws NotFound404Exception {
		return routes.getRoute(service, route);
	}

	@Override
	public String getRemotePath(String service, String route) throws NotFound404Exception {
		return routes.getRemotePath(service, route);
	}

	@Override
	public String getRemotePath(String s, String r, Map<String, String> params) throws NotFound404Exception {
		return Str.sub("$?$", getRemotePath(s, r), UrlTool.getQueryString(params));
	}

	@Override
	public boolean isAccessible(String service, String route, RequestContext context) throws NotFound404Exception {
		return routes.isAccessible(service, route, context);
	}

	@Override
	public String getRemoteAddress() {
		String protocol = routes.isHttps() ? "https" : "http";
		String root = Str.sub("$://$", protocol, routes.getHost());
		return UrlTool.append(root, routes.getServletPrefix());
	}

	@Override
	public String getHost() {
		return routes.getHost();
	}
}
