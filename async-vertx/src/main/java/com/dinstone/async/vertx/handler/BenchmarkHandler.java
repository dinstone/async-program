package com.dinstone.async.vertx.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class BenchmarkHandler implements Handler<RoutingContext> {

	@Override
	public void handle(RoutingContext rc) {
		rc.response().end("OK @ " + Thread.currentThread().getName());
	}

}
