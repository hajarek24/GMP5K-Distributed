#!/bin/bash  
# Réserver des nœuds  
oarsub -I -l nodes=3,walltime=1:00:00  
  
# Déployer les workers sur chaque nœud  
for i in {0..2}; do  
    ssh node-$i "cd ~/GMP5K-Distributed && java -cp bin workers.WorkerServer $i 1099" &  
done  
  
# Lancer les benchmarks  
java -cp bin benchmark.PingPongClient node-0 1099