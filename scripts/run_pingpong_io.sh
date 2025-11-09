#!/bin/bash  
  
# Script pour lancer les mesures ping-pong + I/O  
  
if [ $# -lt 2 ]; then  
    echo "Usage: $0 <hostA:portA> <hostB:portB>"  
    exit 1  
fi  
  
HOSTA=$(echo $1 | cut -d: -f1)  
PORTA=$(echo $1 | cut -d: -f2)  
HOSTB=$(echo $2 | cut -d: -f1)  
PORTB=$(echo $2 | cut -d: -f2)  
  
mkdir -p results  
mkdir -p test_files  
  
# Créer des fichiers de test de différentes tailles  
SIZES=(1024 10240 102400 1048576 10485760)  
  
for size in "${SIZES[@]}"; do  
    dd if=/dev/urandom of=test_files/input_${size}.dat bs=$size count=1 2>/dev/null  
done  
  
# Démarrer le serveur  
ssh $HOSTB "cd ~/GMP5K-Distributed && nohup java -cp bin benchmark.PingPongServer $PORTB > pingpong_io_server.log 2>&1 &"  
  
sleep 2  
  
echo "Lancement des tests ping-pong + I/O..."  
for size in "${SIZES[@]}"; do  
    echo "Test avec fichier de taille: $size bytes"  
    java -cp bin benchmark.PingPongIO $HOSTA $PORTA $HOSTB $PORTB \  
        test_files/input_${size}.dat results/output_${size}.dat >> results/pingpong_io_results.txt  
done  
  
echo "Tests terminés. Résultats dans results/pingpong_io_results.txt"