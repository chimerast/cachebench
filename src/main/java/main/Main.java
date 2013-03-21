package main;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.TimeInstrument;

import cache.HashMapCache;
import cache.ICache;
import cache.LocalMemcachedCache;
import cache.NopCache;
import cache.RemoteMemcachedCache;

public class Main {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {

        Variables.COUNT = Integer.parseInt(args[0]);
        Variables.LOOP = Integer.parseInt(args[1]);
        Variables.THREADS = Integer.parseInt(args[2]);

        Class<? extends ICache>[] caches =
            new Class[] {
                NopCache.class,
                LocalMemcachedCache.class,
                RemoteMemcachedCache.class,
                HashMapCache.class };

        for (Class<? extends ICache> cache : caches) {
            Bench.prepare(cache.newInstance());

            ExecutorService executor =
                Executors.newFixedThreadPool(Variables.THREADS);

            List<Future<Profiler>> futures = new ArrayList<>();
            for (int i = 0; i < Variables.THREADS; ++i) {
                futures.add(executor.submit(new Bench(cache.newInstance())));
            }

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.DAYS);

            Map<String, SummaryStatistics> map =
                new LinkedHashMap<String, SummaryStatistics>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public SummaryStatistics get(Object key) {
                        SummaryStatistics ret = super.get(key);
                        if (ret == null) {
                            super.put((String) key, ret =
                                new SummaryStatistics());
                        }
                        return ret;
                    }
                };

            for (Future<Profiler> future : futures) {
                Profiler p = future.get();
                for (TimeInstrument ti : p.getCopyOfChildTimeInstruments()) {
                    map.get(ti.getName()).addValue(ti.elapsedTime());
                }
            }

            System.out.println(String.format(
                "%s: count=%d, loop=%d, threads=%d",
                cache.getSimpleName(),
                Variables.COUNT,
                Variables.LOOP,
                Variables.THREADS));
            for (Entry<String, SummaryStatistics> e : map.entrySet()) {
                SummaryStatistics stat = e.getValue();
                System.out.println(String.format(
                    "%-6.6s: mean=%8.3fms, min=%8.3fms, max=%8.3fms",
                    e.getKey(),
                    stat.getMean() / 1000.0 / 1000.0,
                    stat.getMin() / 1000.0 / 1000.0,
                    stat.getMax() / 1000.0 / 1000.0));
            }
            System.out.println();
        }

        LocalMemcachedCache.CLIENT.shutdown();
        RemoteMemcachedCache.CLIENT.shutdown();
    }
}
