package com.eriklievaart.javalightning.bundle.route;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;

import com.eriklievaart.javalightning.bundle.api.RequestContext;
import com.eriklievaart.javalightning.bundle.api.exception.RouteUnavailableException;
import com.eriklievaart.javalightning.bundle.api.page.PageService;
import com.eriklievaart.javalightning.bundle.api.page.Route;
import com.eriklievaart.javalightning.bundle.api.page.RouteType;
import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.lang.api.AssertionException;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.pattern.WildcardTool;
import com.eriklievaart.toolkit.lang.api.str.Str;

public class RouteIndex {

	private final String serviceId;
	private Map<String, Route> pathToRoute = NewCollection.concurrentHashMap();
	private Map<String, Route> idToRoute = NewCollection.concurrentHashMap();
	private List<Route> wildcardMappings = NewCollection.concurrentList();
	private BiPredicate<Route, RequestContext> predicate;

	public RouteIndex(PageService service) {
		this.serviceId = service.getPrefix();
		this.predicate = service.getAccessible();
		for (Route route : service.getRoutes()) {
			installRoute(route);
		}
	}

	private void installRoute(Route route) {
		String path = route.getPath();
		AssertionException.on(idToRoute.containsKey(route.getId()), "duplicate id %", path);
		AssertionException.on(pathToRoute.containsKey(path), "duplicate path %", path);
		idToRoute.put(route.getId(), route);

		if (route.getPath().contains("*")) {
			wildcardMappings.add(route);

		} else {
			for (RouteType method : route.getTypes()) {
				pathToRoute.put(createKey(method, path), route);
			}
		}
	}

	public Optional<SecureRoute> resolve(RouteType method, String path) {
		String key = createKey(method, UrlTool.removeTrailingSlash(path));
		if (pathToRoute.containsKey(key)) {
			return Optional.of(new SecureRoute(pathToRoute.get(key), predicate));
		}
		for (Route route : wildcardMappings) {
			if (route.getTypes().contains(method) && WildcardTool.match(route.getPath(), path == null ? "" : path)) {
				return Optional.of(new SecureRoute(route, predicate));
			}
		}
		return Optional.empty();
	}

	private String createKey(RouteType method, String path) {
		Check.notNull(method);
		return Str.sub("$:$", method, path == null ? "" : path);
	}

	public Route getRoute(String routeId) throws RouteUnavailableException {
		Route route = idToRoute.get(routeId);
		if (route == null) {
			throw new RouteUnavailableException(getUnavailableMessage(routeId));
		}
		return route;
	}

	boolean isAccessible(String routeId, RequestContext context) throws RouteUnavailableException {
		if (!idToRoute.containsKey(routeId)) {
			throw new RouteUnavailableException(getUnavailableMessage(routeId));
		}
		return predicate.test(idToRoute.get(routeId), context);
	}

	private String getUnavailableMessage(String id) {
		return Str.sub("No route with id % in service %", id, serviceId);
	}
}
