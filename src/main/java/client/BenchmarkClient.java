package client;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class BenchmarkClient {
    private static final int NUM_REQUESTS = 1000;
    private static final int CONCURRENT_CLIENTS = 50;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java client.BenchmarkClient <port>");
            System.out.println("Ports: 8001 (single), 8002 (thread-per-request), 8003 (pool)");
            return;
        }

        int port = Integer.parseInt(args[0]);
        benchmark(port);
    }

    private static void benchmark(int port) {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_CLIENTS);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        CountDownLatch latch = new CountDownLatch(NUM_REQUESTS);

        for (int i = 0; i < NUM_REQUESTS; i++) {
            executor.submit(() -> {
                try {
                    sendRequest(port);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        double totalTime = (endTime - startTime) / 1000.0;
        double throughput = NUM_REQUESTS / totalTime;

        System.out.println("=== Benchmark Results ===");
        System.out.println("Port: " + port);
        System.out.println("Total requests: " + NUM_REQUESTS);
        System.out.println("Concurrent clients: " + CONCURRENT_CLIENTS);
        System.out.println("Successful: " + successCount.get());
        System.out.println("Errors: " + errorCount.get());
        System.out.println("Total time: " + String.format("%.2f", totalTime) + " seconds");
        System.out.println("Throughput: " + String.format("%.2f", throughput) + " req/sec");

        executor.shutdown();
    }

    private static void sendRequest(int port) throws IOException {
        try (Socket socket = new Socket("localhost", port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("READ");
            in.readLine();
        }
    }
}
