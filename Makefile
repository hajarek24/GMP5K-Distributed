all: clean count
  
clean:
	rm -f result.txt
  
count:
	java -cp bin workers.DistributedWordCount input.txt result.txt localhost:8001 localhost:8002 localhost:8003
