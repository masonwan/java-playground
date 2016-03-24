package masonwan.playground;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.assertj.core.api.Fail;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

@Log4j2
public class PlayFuture {
    @BeforeClass
    public static void setUpClass() throws Exception {
        Configurator.setRootLevel(Level.DEBUG);
    }

    @Test
    public void testGet() throws Exception {
        CompletableFuture<Integer> future = CompletableFuture
            .supplyAsync(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }

                return 123;
            })
            .exceptionally((ex) -> {
                Fail.fail("Should not happen");
                return null;
            });

        assertThat(future.get())
            .isEqualTo(123);
    }

    @Test
    public void testExceptionally() throws Exception {
        CompletableFuture<Integer> future = CompletableFuture
            .<Integer>supplyAsync(() -> {
                throw new RuntimeException("Shit happens");
            })
            .exceptionally((ex) -> 12)
            .whenComplete((result, ex) -> {
                assertThat(result).isEqualTo(12);
                assertThat(ex).isNull();
            });
    }

    @Test
    public void testHandle() throws Exception {
        CompletableFuture<Integer> future = CompletableFuture
            .<Integer>supplyAsync(() -> {
                throw new RuntimeException("Shit happens");
            })
            .handle((num, ex) -> {
                assertThat(ex).hasCauseInstanceOf(RuntimeException.class);
                assertThat(num).isNull();
                return 456;
            });

        assertThat(future.get()).isEqualTo(456);
    }

    @Test
    public void testFailThen() throws Exception {
        CompletableFuture<Integer> future = CompletableFuture
            .supplyAsync(() -> {
                throw new RuntimeException();
            });

        Exception exception = null;
        try {
            future.get();
            Fail.fail("Should throw an exception");
        } catch (Exception e) {
            exception = e;
            log.debug(exception);
        }

        assertThat(exception).isNotNull();
    }

    @Test
    public void testFutureList() throws Exception {
        ArrayList<CompletableFuture<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final int finalI = i;
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                int count = 0;
                while (++count < 10) {
                    try {
                        log.debug("Working on {}", finalI);
                        Thread.sleep(10);
                    } catch (InterruptedException ignored) {
                    }
                }
                return finalI;
            });
            futures.add(future);
        }

        log.debug("Should log first");

        CompletableFuture<List<Integer>> future = CompletableFuture.allOf(Iterables.toArray(futures, CompletableFuture.class))
            .thenApply(aVoid -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));

        List<Integer> results = future.get();
        for (int i = 0; i < results.size(); i++) {
            assertThat(results.get(i)).isEqualTo(i);
        }

        log.debug("Done");
    }

    @Test
    public void testChain() throws Exception {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            int count = 0;
            while (++count < 10) {
                try {
                    log.debug("Working on {}", 1);
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
            return 1;
        });
        future = future.thenApply(integer -> {
            int count = 0;
            while (++count < 10) {
                try {
                    log.debug("Working on {}", 2);
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
            return 2;
        });
        assertThat(future.get()).isEqualTo(2);
        log.debug("Done");
    }

    @Test
    public void testParallel() throws Exception {
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(getSupplier(1));
        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(getSupplier(2));
        int result = CompletableFuture.<String>allOf(future1, future2)
            .thenApply(aVoid -> {
                log.debug("Complete both");
                return 3;
            })
            .get();
        assertThat(future1.get()).isEqualTo(1);
        assertThat(future2.get()).isEqualTo(2);
        assertThat(result).isEqualTo(3);
        log.debug("Done");
    }

    @Test
    public void testFutureList_oneFails() throws Exception {
        ArrayList<CompletableFuture<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(getSupplier(i));
            futures.add(future);
        }
        futures.add(CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException();
        }));

        log.debug("Should log first");

        CompletableFuture<List<Integer>> future = CompletableFuture
            .allOf(Iterables.toArray(futures, CompletableFuture.class))
            .exceptionally(ex -> {
                log.debug("exceptionally:", ex);
                return null;
            })
            .whenComplete((aVoid, throwable) -> {
                log.debug("when complete 1");
                log.debug("aVoid:", aVoid);
                log.debug("throwable:", throwable);
            })
            .thenApply(aVoid -> {
                return Lists.newArrayList();
            });

        Object o = future.get();
        log.debug("future.get", o);

        log.debug("Should not happen before everything is done");
    }

    private Supplier<Integer> getSupplier(int id) {
        return () -> {
            int count = 0;
            while (++count < 10) {
                try {
                    log.debug("Working on {}", id);
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            }
            return id;
        };
    }
}
