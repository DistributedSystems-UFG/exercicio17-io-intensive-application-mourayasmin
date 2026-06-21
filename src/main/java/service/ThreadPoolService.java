package service;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.concurrent.*;

public class ThreadPoolService implements Runnable {
    private final ServerSocket serverSocket;
    private final ExecutorService pool;
    private static final String DATA_FILE = "data.txt";

    public ThreadPoolService(int port, int poolSize) throws IOException {
        serverSocket = new ServerSocket(port);
        pool = Executors.newFixedThreadPool(poolSize);
    }

    public void run() {
        System.out.println("Thread pool service started on port " + serverSocket.getLocalPort());
        try {
            while (true) {
                pool.execute(new Handler(serverSocket.accept()));
            }
        } catch (IOException ex) {
            pool.shutdown();
        }
    }

    static class Handler implements Runnable {
        private final Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                String request = in.readLine();
                if ("READ".equals(request)) {
                    String data = Files.readString(Paths.get(DATA_FILE));
                    out.println(data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 8003;
        int poolSize = 10;
        new Thread(new ThreadPoolService(port, poolSize)).start();
    }
}
