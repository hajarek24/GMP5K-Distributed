#!/usr/bin/env python3  
  
import matplotlib.pyplot as plt  
import re  
  
def parse_results(filename):  
    sizes = []  
    times = []  
    throughputs = []  
      
    with open(filename, 'r') as f:  
        for line in f:  
            match = re.search(r'Size: (\d+) bytes, Time: ([\d.]+) ms.*Throughput: ([\d.]+) MB/s', line)  
            if match:  
                sizes.append(int(match.group(1)))  
                times.append(float(match.group(2)))  
                throughputs.append(float(match.group(3)))  
      
    return sizes, times, throughputs