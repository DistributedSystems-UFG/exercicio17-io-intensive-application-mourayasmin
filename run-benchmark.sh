#!/bin/bash

echo "=== Compilando ==="
./compile.sh

echo ""
echo "=== Iniciando servidores ==="
echo "Single-threaded (porta 8001)..."
java -cp bin service.SingleThreadedService &
PID1=$!
sleep 2

echo "Thread-per-request (porta 8002)..."
java -cp bin service.ThreadPerRequestService &
PID2=$!
sleep 2

echo "Thread pool (porta 8003)..."
java -cp bin service.ThreadPoolService &
PID3=$!
sleep 2

echo ""
echo "=== Executando benchmarks ==="
echo ""
echo "--- Single-threaded (porta 8001) ---"
java -cp bin client.BenchmarkClient 8001

echo ""
echo "--- Thread-per-request (porta 8002) ---"
java -cp bin client.BenchmarkClient 8002

echo ""
echo "--- Thread pool (porta 8003) ---"
java -cp bin client.BenchmarkClient 8003

echo ""
echo "=== Parando servidores ==="
kill $PID1 $PID2 $PID3

echo "Benchmark concluído!"
