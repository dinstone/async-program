package com.dinstone.async.vertx.verticle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.async.vertx.handler.AccessLogHandler;
import com.dinstone.async.vertx.handler.AsyncTaskHandler;
import com.dinstone.async.vertx.handler.BenchmarkHandler;
import com.dinstone.async.vertx.handler.HelloHandler;
import com.dinstone.vertx.web.RouterBuilder;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class HttpServerVerticle extends AbstractVerticle {

	private static final Logger LOG = LoggerFactory.getLogger(HttpServerVerticle.class);

	private JsonObject config;

	@Override
	public void init(Vertx vertx, Context context) {
		super.init(vertx, context);
		config = config();
	}

	@Override
	public void start(Future<Void> startFuture) {
		Router mainRouter = Router.router(vertx);
		mainRouter.route().failureHandler(rc -> {
			LOG.error("handler logic occur error", rc.failure());
			rc.response().end();
		});

		mainRouter.route().handler(new AccessLogHandler());
		mainRouter.route().handler(BodyHandler.create());

		Router subRouter = Router.router(vertx);
		subRouter.route("/benchmark").handler(new BenchmarkHandler());
		subRouter.route("/asynctask").handler(new AsyncTaskHandler(vertx));
		mainRouter.mountSubRouter("/async-vertx", subRouter);

		RouterBuilder routerBuilder = RouterBuilder.create(vertx);
		routerBuilder.handler(new HelloHandler());
		mainRouter.mountSubRouter("/api", routerBuilder.build());

		int serverPort = config.getInteger("web.http.port", 8180);
		HttpServerOptions serverOptions = new HttpServerOptions().setIdleTimeout(180);
		vertx.createHttpServer(serverOptions).requestHandler(mainRouter::accept).listen(serverPort, ar -> {
			if (ar.succeeded()) {
				LOG.info("start web http success, web.http.port={}", serverPort);
				startFuture.complete();
			} else {
				LOG.error("start web http failed, web.http.port={}", serverPort);
				startFuture.fail(ar.cause());
			}
		});
	}
}
