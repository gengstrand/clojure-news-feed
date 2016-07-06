package info.glennengstrand.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogVerticle extends AbstractVerticle {
	private HttpClient client = null;
	private Optional<String> elasticSearchHost = Optional.empty();
	private Optional<String> elasticSearchIndex = Optional.empty();
	private Optional<String> elasticSearchType = Optional.empty();
	private Optional<Integer> elasticSearchPort = Optional.empty();
	private Optional<Long> elasticSearchBatchInterval = Optional.empty();
	private final Timer updateElasticSearchBatchJob = new Timer(true);
	private final Random r = new Random(Instant.now().getEpochSecond());
	private final List<PerformanceLogItem> logItems = new LinkedList<PerformanceLogItem>();
	private static final Logger log = Logger.getLogger(LogVerticle.class.getCanonicalName());
	private static final long UPDATE_ELASTICSEARCH_DURATION = 5000l;
	
	private void sendToElasticSearch(String path, String msg) {
		log.finest(String.format("sending %s to %s\n", msg, path));
		client.put(elasticSearchPort.orElse(9200), elasticSearchHost.orElse("localhost"), path, response -> {
			int rc = response.statusCode();
			if (rc >= 300) {
				log.warning(String.format("return status from elasticsearch is %d and diagnostic is %s\n", rc, response.statusMessage()));
			}
		}).end(msg);
	}
	private void sendToElasticSearch(PerformanceLogItem item) {
		String path = String.format("/%s/%s/%s", elasticSearchIndex.orElse("performance"), elasticSearchType.orElse("feed"), item.getId());
		String msg = item.toString();
		sendToElasticSearch(path, msg);
	}
	
  @Override
  public void start() {
	  Optional<JsonObject> es = Optional.ofNullable(config().getJsonObject("elasticsearch"));
	  if (es.isPresent()) {
		  elasticSearchHost = Optional.ofNullable(es.get().getString("host"));
		  elasticSearchPort = Optional.ofNullable(es.get().getInteger("port"));
		  elasticSearchIndex = Optional.ofNullable(es.get().getString("index"));
		  elasticSearchType = Optional.ofNullable(es.get().getString("type"));
		  elasticSearchBatchInterval = Optional.ofNullable(es.get().getLong("interval"));
	  }
	  Optional<Integer> port = Optional.ofNullable(config().getInteger("port"));
	  client = vertx.createHttpClient();
	  updateElasticSearchBatchJob.schedule(new UpdateElasticSearch(), elasticSearchBatchInterval.orElse(UPDATE_ELASTICSEARCH_DURATION), elasticSearchBatchInterval.orElse(UPDATE_ELASTICSEARCH_DURATION));
	  vertx.createHttpServer().requestHandler(req -> {
		req.bodyHandler(buffer -> {
			Optional<JsonObject> body = Optional.ofNullable(buffer.toJsonObject());
			if (body.isPresent()) {
				Optional<JsonObject> request = Optional.ofNullable(body.get().getJsonObject("request"));
				if (request.isPresent()) {
					Optional<String> uri = Optional.ofNullable(request.get().getString("uri"));
					if (uri.isPresent()) {
						String[] upart = uri.get().split("/");
						String entity = upart[uri.get().startsWith("/") ? 1 : 0];
						Optional<String> method = Optional.ofNullable(request.get().getString("method"));
						Optional<JsonObject> response = Optional.ofNullable(body.get().getJsonObject("response"));
						if (response.isPresent()) {
							Optional<Integer> status = Optional.ofNullable(response.get().getInteger("status"));
							Optional<JsonObject> latencies = Optional.ofNullable(body.get().getJsonObject("latencies"));
							if (latencies.isPresent()) {
								Optional<Long> duration = Optional.ofNullable(latencies.get().getLong("request"));
								if (method.isPresent() && status.isPresent() && duration.isPresent()) {
									synchronized(logItems) {
										logItems.add(new PerformanceLogItem(entity, method.get(), status.get(), duration.get()));
									}
								} else {
									log.warning("method, status, and/or duration is missing in " + body);
								}
							} else {
								log.warning("no latencies found in " + body);
							}
						} else {
							log.warning("no response attribute found in " + body);
						}
					} else {
						log.warning("no uri found in " + body);
					}
				} else {
					log.warning("no request attribute found in " + body);
				}
			} else {
				log.warning("no response body");
			}
		});
		req.response().end();
	  }).listen(port.orElse(8888));
  }
  private class PerformanceLogItem {
	  private final String entity;
	  private final String method;
	  private final int status;
	  private final long duration;
	  public PerformanceLogItem(String entity, String method, int status, long duration) {
		  this.entity = entity;
		  this.method = method;
		  this.status = status;
		  this.duration = duration;
	  }
	public String getEntity() {
		return entity;
	}
	public String getMethod() {
		return method;
	}
	public int getStatus() {
		return status;
	}
	public long getDuration() {
		return duration;
	}
	private String getId() {
		return String.format("%s-%s-%d-%d", getEntity(), getMethod(), System.currentTimeMillis(), Math.abs(r.nextInt()));
	}

	public String getMeta() {
		Map<String, Object> outer = new HashMap<String, Object>();
		Map<String, Object> inner = new HashMap<String, Object>();
		inner.put("_index", elasticSearchIndex.orElse("performance"));
		inner.put("_type", elasticSearchType.orElse("feed"));
		inner.put("_id", getId());
		outer.put("index", inner);
		return new JsonObject(outer).toString();
	}
	@Override
	public String toString() {
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("entity", getEntity());
		body.put("operation", getMethod());
		body.put("status", getStatus());
		body.put("duration", getDuration());
		body.put("ts", Instant.now().toString());
		return new JsonObject(body).toString();
	}
  }
  private class UpdateElasticSearch extends TimerTask {

	@Override
	public void run() {
		try {
			synchronized(logItems) {
				if (!logItems.isEmpty()) {
				    Optional<String> batchBody = logItems.stream().map(item -> item.getMeta() + "\n" + item.toString() + "\n").reduce(String::concat);
				    if (batchBody.isPresent()) {
					sendToElasticSearch("/_bulk", batchBody.get());
				    }
					logItems.clear();
				}
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "encountered error while processing elastic search batch index: ", e);
		}
	}
	  
  }
}

