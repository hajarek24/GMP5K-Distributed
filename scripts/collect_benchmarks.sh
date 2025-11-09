#!/bin/bash  
  
# Nettoyer les serveurs existants  
ssh gros-98 "pkill -f PingPong" 2>/dev/null  
sleep 1  
  
echo "size_bytes,throughput_mbps,type" > benchmark_results.csv  
  
# Benchmark sans I/O  
echo "=== Benchmark sans I/O ==="  
ssh gros-98 "cd ~/GMP5K-Distributed && java -cp bin benchmark.PingPongServer 1099" &  
sleep 3  
  
java -cp ~/GMP5K-Distributed/bin benchmark.PingPongClient gros-98.nancy.grid5000.fr 1099 | \  
  grep "Taille:" | awk '{print $2","$5",no_io"}' >> benchmark_results.csv  
  
ssh gros-98 "pkill -f PingPongServer"  
sleep 2  
  
# Benchmark avec I/O  
echo "=== Benchmark avec I/O ==="  
ssh gros-98 "cd ~/GMP5K-Distributed && java -cp bin benchmark.PingPongIOServer 1100" &  
sleep 3  
  
java -cp ~/GMP5K-Distributed/bin benchmark.PingPongIOClient gros-98.nancy.grid5000.fr 1100 | \  
  grep "Taille:" | awk '{print $2","$6",with_io"}' >> benchmark_results.csv  
  
ssh gros-98 "pkill -f PingPongIOServer"  
  
echo "Résultats sauvegardés dans benchmark_results.csv"  
cat benchmark_results.csv