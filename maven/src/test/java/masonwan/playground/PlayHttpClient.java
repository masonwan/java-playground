package masonwan.playground;

import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.assertj.core.api.Condition;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@Log4j2
public class PlayHttpClient {
    CloseableHttpClient httpclient;

    @BeforeMethod
    public void setUp() throws Exception {
        Configurator.setRootLevel(Level.DEBUG);

        httpclient = HttpClients.createDefault();
    }

    @AfterMethod
    public void tearDown() throws Exception {
        httpclient.close();
    }

    @Test
    public void testHttpGet() throws Exception {
        CompletableFuture<String> promise = getHttpResource("https://www.example.com");
        promise
            .exceptionally(throwable -> {
                log.error("Fail to get HTTP resource. {}", throwable.getClass());
                return null;
            });
        promise
            .thenAccept(responseBody -> {
                log.debug("response is {} characters long", responseBody.length());
            });

        promise.get(1, TimeUnit.SECONDS);
    }

    @Test
    public void testHttpGet_throws() throws Exception {
        CompletableFuture<String> promise = getHttpResource("http://path.does.not.exist");
        promise
            .exceptionally(throwable -> {
                log.error("Fail to get HTTP resource. {}", throwable.getClass());
                return null;
            });

        Throwable throwable = catchThrowable(() -> {
            promise.get(1, TimeUnit.SECONDS);
        });

        Condition<Throwable> condition = new Condition<>(e -> e.getCause() instanceof RuntimeException && e.getCause().getCause() instanceof UnknownHostException, "Foo");
        assertThat(throwable)
            .isInstanceOf(ExecutionException.class)
            .is(condition);
    }

    @Test
    public void testHttpGet_timeout() throws Exception {
        CompletableFuture<String> promise = getHttpResource("https://www.example.com")
            .thenApply(responseBody -> {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ignored) {
                }
                return "";
            });
        promise
            .exceptionally(throwable -> {
                log.error("Fail to get HTTP resource. {}", throwable.getClass());
                return null;
            });

        Throwable throwable = catchThrowable(() -> {
            promise.get(100, TimeUnit.MILLISECONDS);
        });

        assertThat(throwable)
            .isInstanceOf(TimeoutException.class);
    }

    private CompletableFuture<String> getHttpResource(String url) {
        return CompletableFuture.supplyAsync(() -> {
            HttpGet httpget = new HttpGet(URI.create(url));
            try {
                return httpclient.execute(httpget, response -> {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
