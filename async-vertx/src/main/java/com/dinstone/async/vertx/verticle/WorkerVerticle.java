package com.dinstone.async.vertx.verticle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class WorkerVerticle extends AbstractVerticle {
	private static final Logger LOG = LoggerFactory.getLogger(WorkerVerticle.class);

	private JsonObject config;

	@Override
	public void init(Vertx vertx, Context context) {
		super.init(vertx, context);
		config = config();
	}

	@Override
	public void start(Future<Void> startFuture) {
		LOG.info("worker verticle start");
		vertx.eventBus().consumer("hello", message -> {
			LOG.info("message body : " + message.body());
			
			message.reply("handle message ok");
		});
		startFuture.complete();
	}
}
