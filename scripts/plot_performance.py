import pandas as pd  
import matplotlib.pyplot as plt  
  
# Lire les données  
df = pd.read_csv('benchmark_results.csv')  
no_io = df[df['type'] == 'no_io']  
with_io = df[df['type'] == 'with_io']  
  
# Créer une figure avec 3 sous-graphiques  
plt.figure(figsize=(18, 5))  
  
# Graphique 1 : Comparaison des débits  
plt.subplot(1, 3, 1)  
plt.plot(no_io['size_bytes'], no_io['throughput_mbps'], 'o-', label='Sans I/O', linewidth=2)  
plt.plot(with_io['size_bytes'], with_io['throughput_mbps'], 's-', label='Avec I/O', linewidth=2)  
plt.xscale('log')  
plt.yscale('log')  
plt.xlabel('Taille du message (bytes)')  
plt.ylabel('Débit (MB/s)')  
plt.title('Comparaison Débit\n(même site Nancy)')  
plt.legend()  
plt.grid(True)  
  
# Graphique 2 : Comparaison des latences  
plt.subplot(1, 3, 2)  
# Calculer la latence approximative : temps = taille / débit  
latency_no_io = (no_io['size_bytes'] / (no_io['throughput_mbps'] * 1024 * 1024)) * 1000  # en ms  
latency_with_io = (with_io['size_bytes'] / (with_io['throughput_mbps'] * 1024 * 1024)) * 1000  # en ms  
  
plt.plot(no_io['size_bytes'], latency_no_io, 'o-', label='Sans I/O', linewidth=2)  
plt.plot(with_io['size_bytes'], latency_with_io, 's-', label='Avec I/O', linewidth=2)  
plt.xscale('log')  
plt.yscale('log')  
plt.xlabel('Taille du message (bytes)')  
plt.ylabel('Temps de transfert (ms)')  
plt.title('Comparaison Latence\n(même site Nancy)')  
plt.legend()  
plt.grid(True)  
  
# Graphique 3 : Impact de l'I/O en pourcentage  
plt.subplot(1, 3, 3)  
impact = ((no_io['throughput_mbps'].values - with_io['throughput_mbps'].values) /   
          no_io['throughput_mbps'].values * 100)  
plt.bar(range(len(impact)), impact)  
plt.xticks(range(len(impact)), ['1KB', '10KB', '100KB', '1MB'])  
plt.ylabel('Réduction de débit (%)')  
plt.title('Impact de l\'I/O\n(même site Nancy)')  
plt.grid(True, axis='y')  
  
plt.tight_layout()  
plt.savefig('comparison_same_site.png', dpi=300, bbox_inches='tight')  
print("✓ Graphique généré : comparison_same_site.png")  
  
# Afficher les statistiques  
print("\n=== Résumé des performances ===")  
print(f"Débit max (sans I/O) : {no_io['throughput_mbps'].max():.2f} MB/s")  
print(f"Débit max (avec I/O) : {with_io['throughput_mbps'].max():.2f} MB/s")  
print(f"Latence min (sans I/O) : {latency_no_io.min():.2f} ms")  
print(f"Latence min (avec I/O) : {latency_with_io.min():.2f} ms")