# Compiling and running
To compile this project, first ensure JDK 8 or later is installed. Then, open a terminal in this directory, and execute:
```sh
javac -d ./bin/ ./src/*.java
```

To then run the program using the spec's parameters, simply execute:
```sh
java -cp ./bin App
```

If you want to use different parameters, use this format:
```sh
java -cp ./bin App [max [threadCount [numTopPrimes [filename]]]]
```
where:
- `max` is the maximum value to test (inclusive) - default `100000000` (10^8)
- `threadCount` is the number of threads to spawn - default `8`
- `numTopPrimes` is the maximum number of primes to print the values of - default `10`
- `filename` is the file to output to, where a value of `-` means standard output - default `primes.txt`.

# Correctness

# Experimental evaluation
