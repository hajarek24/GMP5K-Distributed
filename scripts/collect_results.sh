#!/bin/bash  
  
# Script pour collecter et résumer les résultats  
  
echo "=== Résumé des résultats Ping-Pong ==="  
echo ""  
echo "Taille (bytes) | Temps (ms) | RTT (ms) | Débit (MB/s)"  
echo "------------------------------------------------------------"  
  
if [ -f results/pingpong_results.txt ]; then  
    cat results/pingpong_results.txt  
fi  
  
echo ""  
echo "=== Résumé des résultats Ping-Pong + I/O ==="  
echo ""  
  
if [ -f results/pingpong_io_results.txt ]; then  
    cat results/pingpong_io_results.txt  
fi