import pandas as pd  
import matplotlib.pyplot as plt  
  
df = pd.read_csv('benchmark_intersites.csv')  
  
fig, ((ax1, ax2), (ax3, ax4)) = plt.subplots(2, 2, figsize=(14, 10))  
  
# Graphique 1: Latence  
ax1.bar(df['site_pair'], df['latency_ms'], color='coral')  
ax1.set_ylabel('Latence (ms)')  
ax1.set_title('Comparaison Latence Inter-sites')  
ax1.grid(True, axis='y')  
  
# Graphique 2: RTT  
ax2.bar(df['site_pair'], df['rtt_ms'], color='skyblue')  
ax2.set_ylabel('RTT (ms)')  
ax2.set_title('Comparaison RTT Inter-sites')  
ax2.grid(True, axis='y')  
  
# Graphique 3: Débit 1KB  
ax3.bar(df['site_pair'], df['throughput_1kb'], color='lightgreen')  
ax3.set_ylabel('Débit (MB/s)')  
ax3.set_title('Débit pour 1KB')  
ax3.grid(True, axis='y')  
  
# Graphique 4: Débit 1MB  
ax4.bar(df['site_pair'], df['throughput_1mb'], color='plum')  
ax4.set_ylabel('Débit (MB/s)')  
ax4.set_title('Débit pour 1MB')  
ax4.grid(True, axis='y')  
  
plt.tight_layout()  
plt.savefig('comparison_intersites.png', dpi=300, bbox_inches='tight')  
print("✓ Graphique généré : comparison_intersites.png")  
  
print("\n=== Résumé des performances inter-sites ===")  
print(f"Nancy → Lyon : Latence {df.loc[0, 'latency_ms']:.2f} ms, Débit 1MB {df.loc[0, 'throughput_1mb']:.2f} MB/s")  
print(f"Nancy → Grenoble : Latence {df.loc[1, 'latency_ms']:.2f} ms, Débit 1MB {df.loc[1, 'throughput_1mb']:.2f} MB/s")  