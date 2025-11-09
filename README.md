# GMP5K-Distributed

## Vue d'ensemble

Ce projet implémente un système de **make distribué** qui permet d'exécuter des tâches en parallèle sur plusieurs nœuds workers. Contrairement au système séquentiel (voir [hajarek24/GMP5K](https://github.com/hajarek24/GMP5K)), ce système distribue le traitement ligne par ligne à différents workers pour paralléliser l'exécution.

## Architecture

Le système est composé de trois couches principales :

### 1. Couche de Parsing
- **`MakefileParser`** : Parse les fichiers Makefile et extrait les dépendances et commandes
- Support de plusieurs commandes par cible via `Map<String, List<String>>`

### 2. Couche de Distribution
- **`DistributedWordCount`** : Coordonne la distribution des lignes aux workers
- **`WorkerServer`** : Serveur qui écoute sur un port et traite les requêtes
- **`LineWorker`** : Traite une ligne de texte et compte les mots

### 3. Couche d'Orchestration
- **`DistributedExecutor`** : Point d'entrée principal qui orchestre l'exécution
- **`DistributedTaskGraph`** : Gère le graphe de dépendances et l'exécution distribuée

## Structure du Projet

```
GMP5K-Distributed/
├── src/
│   ├── workers/
│   │   ├── LineWorker.java           # Traite une ligne de texte
│   │   ├── WorkerServer.java         # Serveur sur chaque nœud
│   │   └── DistributedWordCount.java # Coordonne la distribution
│   ├── parser/
│   │   └── MakefileParser.java       # Parse le Makefile
│   └── distributed/
│       ├── DistributedExecutor.java  # Point d'entrée principal
│       └── DistributedTaskGraph.java # Graphe de tâches distribué
├── bin/                              # Fichiers .class compilés
├── config/
│   └── workers.conf                  # Configuration des workers
├── Makefile                          # Définition des tâches
├── input.txt                         # Fichier d'entrée
└── result.txt                        # Fichier de sortie
```

## Compilation

```bash
# Compiler tous les fichiers Java
javac -d bin src/workers/*.java src/parser/*.java src/distributed/*.java
```

## Test Local (sans Grid5000)

### Étape 1 : Créer le fichier d'entrée

```bash
cat > input.txt << 'EOF'
Hello world this is line one
Second line with more words here
Third line is shorter
Fourth line has exactly five words
Last line contains several additional words for testing
EOF
```

### Étape 2 : Démarrer les workers locaux

Ouvrez 3 terminaux et lancez un worker dans chacun :

**Terminal 1 :**
```bash
java -cp bin workers.WorkerServer 0 8001
```

**Terminal 2 :**
```bash
java -cp bin workers.WorkerServer 1 8002
```

**Terminal 3 :**
```bash
java -cp bin workers.WorkerServer 2 8003
```

### Étape 3 : Exécuter le traitement distribué

Dans un 4ème terminal :

```bash
java -cp bin workers.DistributedWordCount input.txt result.txt localhost:8001 localhost:8002 localhost:8003
```

### Étape 4 : Vérifier le résultat

```bash
cat result.txt
```

**Résultat attendu :**
```
Word count: 30
```

## Exécution via Makefile

Avec les workers actifs, vous pouvez aussi exécuter via le système de make :

```bash
java -cp bin distributed.DistributedExecutor Makefile config/workers.conf
```

## Fonctionnement

### Distribution Ligne par Ligne

Le système distribue chaque ligne de `input.txt` à un worker différent en round-robin :

- Ligne 1 → Worker 0 (localhost:8001)
- Ligne 2 → Worker 1 (localhost:8002)
- Ligne 3 → Worker 2 (localhost:8003)
- Ligne 4 → Worker 0 (localhost:8001)
- Ligne 5 → Worker 1 (localhost:8002)

Chaque worker compte les mots de sa ligne, puis le coordinateur agrège les résultats.

### Parsing du Makefile

Le `MakefileParser` supporte :

- Plusieurs commandes par cible
- Résolution des dépendances
- Syntaxe Makefile standard (tabulations obligatoires)

## Configuration des Workers

Le fichier `config/workers.conf` contient la liste des workers :

```
localhost:8001
localhost:8002
localhost:8003
```

Pour Grid5000, remplacez par les adresses réelles des nœuds :

```
node1.grid5000.fr:8001
node2.grid5000.fr:8002
node3.grid5000.fr:8003
```

## Exemple de Makefile

```makefile
all: clean count

clean:
	rm -f result.txt

count:
	java -cp bin workers.DistributedWordCount input.txt result.txt localhost:8001 localhost:8002 localhost:8003
```

**Important :** Les commandes doivent commencer par une tabulation (pas des espaces).

## Différences avec le Système Séquentiel

| Aspect | Séquentiel (GMP5K) | Distribué (GMP5K-Distributed) |
|--------|-------------------|------------------------------|
| Exécution | Une seule machine | Plusieurs workers en parallèle |
| Traitement | Fichier complet | Ligne par ligne |
| Communication | Processus locaux | Sockets réseau |
| Scalabilité | Limitée | Horizontale (ajout de workers) |

## Technologies Utilisées

- **Java** : Langage principal
- **Sockets** : Communication réseau entre coordinateur et workers
- **ExecutorService** : Gestion des threads pour l'exécution parallèle
- **ProcessBuilder** : Exécution de commandes système

## État Actuel

✅ Parsing de Makefile avec support multi-commandes  
✅ Distribution ligne par ligne aux workers  
✅ Test local avec workers sur localhost  
✅ Agrégation des résultats  
⏳ Déploiement sur Grid5000 (à venir)  
⏳ Scripts d'automatisation pour Grid5000 (à venir)

## Prochaines Étapes

1. Créer des scripts de déploiement pour Grid5000
2. Implémenter la gestion des fichiers distribués
3. Ajouter des mesures de performance (ping-pong, I/O)
4. Optimiser la distribution de charge entre workers
