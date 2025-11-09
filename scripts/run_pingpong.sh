#!/bin/bash  
  
# Script pour lancer les mesures ping-pong  
  
if [ $# -lt 2 ]; then  
    echo "Usage: $0 <hostA:portA> <hostB:portB>"  
    exit 1  
fi  
  
HOSTA=$(echo $1 | cut -d: -f1)  
PORTA=$(echo $1 | cut -d: -f2)  
HOSTB=$(echo $2 | cut -d: -f1)  
PORTB=$(echo $2 | cut -d: -f2)  
  
# Créer le dossier de résultats  
mkdir -p results  
  
# Démarrer le serveur ping-pong sur B  
ssh $HOSTB "cd ~/GMP5K-Distributed && nohup java -cp bin benchmark.PingPongServer $PORTB > pingpong_server.log 2>&1 &"  
  
sleep 2  
  
# Tailles de messages à tester (en bytes)  
SIZES=(1 1024 10240 102400 1048576 10485760)  
  
echo "Lancement des tests ping-pong..."  
for size in "${SIZES[@]}"; do  
    echo "Test avec taille: $size bytes"  
    java -cp bin benchmark.PingPong $HOSTA $PORTA $HOSTB $PORTB $size >> results/pingpong_results.txt  
done  
  
echo "Tests terminés. Résultats dans results/pingpong_results.txt"