#!/bin/bash  
  
# Script de test local  
  
echo "=== Compilation ==="  
javac -d bin src/**/*.java  
  
echo ""  
echo "=== Démarrage des workers ==="  
java -cp bin workers.WorkerServer 0 8001 &  
WORKER1=$!  
java -cp bin workers.WorkerServer 1 8002 &  
WORKER2=$!  
java -cp bin workers.WorkerServer 2 8003 &  
WORKER3=$!  
  
sleep 2  
  
echo ""  
echo "=== Test DistributedWordCount ==="  
java -cp bin workers.DistributedWordCount input.txt result.txt localhost:8001 localhost:8002 localhost:8003  
  
echo ""  
echo "=== Résultat ==="  
cat result.txt  
  
echo ""  
echo "=== Arrêt des workers ==="  
kill $WORKER1 $WORKER2 $WORKER3  
  
echo "Test terminé"