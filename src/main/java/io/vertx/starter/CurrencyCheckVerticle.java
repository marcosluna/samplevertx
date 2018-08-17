package io.vertx.starter;

import io.netty.handler.codec.http.HttpResponse;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author marcos
 * @since 2018-08-16
 */
public class CurrencyCheckVerticle extends AbstractVerticle {

    private String originWsURL = "data.fixer.io";
    private String uripath = "/api/latest?access_key=1731073f2b0f72086851e976b89600f5";

    private String jsonvalue = "{}";
    private HttpClient clientt;

    public static String CONTENT_TYPE = "application/json";

    @Override
    public void start() throws Exception {
        vertx.createHttpServer().requestHandler(req -> retrieveData(req.response(), req.params())).listen(8080);
    }

    /**
     * Retrieves JSON infomration from a general souce and privdes a customized
     * json response
     *
     * @return customized JSON string.
     */
    private String retrieveData(HttpServerResponse response, MultiMap params) {

        clientt = vertx.createHttpClient();

        clientt.getNow(80, originWsURL, uripath, new Handler<HttpClientResponse>() {

            @Override
            public void handle(HttpClientResponse httpClientResponse) {

                httpClientResponse.bodyHandler(new Handler<Buffer>() {
                    @Override
                    public void handle(Buffer buffer) {
                        jsonvalue = buffer.getString(0, buffer.length());
                        System.out.println("52--- " + jsonvalue);
                        jsonvalue = filterCurrencies(jsonvalue);
                        response.headers().add(HttpHeaders.CONTENT_TYPE, CurrencyCheckVerticle.CONTENT_TYPE);
                        response.headers().add(HttpHeaders.CONTENT_LENGTH, String.valueOf(jsonvalue.length()));
                        response.write(jsonvalue);
                        response.end();
                    }

                    /**
                     * @param jsonvalue, a String containing json data
                     * @return a filtered version of the original currencies json object or the same object if cant find a valid currency parameter
                     */
                    private String filterCurrencies(String jsonvalue) {
                        System.out.println("64 there are " + params.size());
                        JsonObject filtered = null;
                        JsonObject jsonObjecjt = null;
                        Object target = null;
                        if (params.size() > 0) {
                            jsonObjecjt = new JsonObject(jsonvalue);

                            if (jsonObjecjt.getJsonObject("rates").containsKey(params.get("currency"))) {
                                target = jsonObjecjt.getJsonObject("rates").getValue(params.get("currency"));
                                System.out.println(target);
                                filtered = new JsonObject();
                                filtered.put("date", jsonObjecjt.getString("date"));
                                String currtime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE) + ":" + Calendar.getInstance().get(Calendar.SECOND);
                                filtered.put("time", currtime);
                                filtered.put("base", jsonObjecjt.getString("base"));
                                filtered.put("target", params.get("currency"));
                                filtered.put("rate",""+target);
                                jsonvalue = filtered.toString();
                            }
                        }
                        return jsonvalue;
                    }
                });
            }
        });

        return jsonvalue;
    }

}
