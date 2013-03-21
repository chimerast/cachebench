package main;

import java.util.Random;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;

import cache.ICache;

public class Bench implements Callable<Profiler> {

    private final ICache cache;

    public Bench(ICache cache) throws Exception {
        this.cache = cache;
    }

    @Override
    public Profiler call() throws Exception {
        Profiler profiler = new Profiler(cache.getClass().getSimpleName());

        Random rand = new Random();

        profiler.start("set");
        for (int i = 0; i < Variables.LOOP; ++i) {
            cache.set(
                Integer.toString(rand.nextInt(Variables.COUNT)),
                createDummyString());
        }

        profiler.start("get");
        for (int i = 0; i < Variables.LOOP; ++i) {
            String s =
                cache.get(Integer.toString(rand.nextInt(Variables.COUNT)));
            if (s == null) {
                new IllegalStateException();
            }
        }

        profiler.stop();
        return profiler;
    }

    private static final char[] DUMMY_DATA = new char[64 * 1024];
    static {
        for (int j = 0; j < DUMMY_DATA.length; ++j) {
            DUMMY_DATA[j] = '0';
        }
    }

    public static String createDummyString() {
        return new String(DUMMY_DATA);
    }

    public static void prepare(ICache cache) throws Exception {
        Logger logger = LoggerFactory.getLogger(Bench.class);

        logger.info("{}: clear cache", cache.getClass().getSimpleName());
        cache.reset();

        logger.info("{}: fill cache with dummy data", cache
            .getClass()
            .getSimpleName());
        for (int i = 0; i < Variables.COUNT; ++i) {
            cache.set(Integer.toString(i), createDummyString());
        }

        logger.info("{}: prepared", cache.getClass().getSimpleName());
    }
}
