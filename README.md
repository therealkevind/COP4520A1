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
I'll call numbers greater than 5 and congruent to either 1 or 5 mod 6 *potential primes*.  
(Yes, 2, 3, and 5 are also primes, but they're hard-coded.)

## Parallelism
The threads have two critical points: when they take a batch of integers to check the potential primes of, and when they add to the set of primes.
- Every potential prime is checked at some point:  
  The threads take a group of 6 integers at a time by calling `getAndAdd(6)` on the `AtomicInteger`. Java guarantees this to be an atomic operation, hence this cannot result in an inconsistent state. They then take the integers at indices 1 and 5 of the group, which are the two potential primes.
- Every number determined to be prime is added correctly to the set of primes:  
  The threads only access the bit set through the `ConcurrentBitSet` class, specifically by using a `synchronized` method to set bits, hence providing mutual exclusion. The `BitSet`'s also only ever read from *after* all the threads have completed. Hence, only one call to any `BitSet` methods occurs at a time, so their effects must be correct. (This is necessary because `BitSet` does *not* guarantee thread safety on its own.)

## Number theory
As for the mathematics, every potential prime has its primality correctly determined:
- Only numbers x &leq; sqrt(n) need be checked for divisibility, because if any number x > sqrt(n) is a proper factor, n/x < sqrt(n) must be as well.
- Every potential prime is congruent to either 1 or 5 mod 6 by definition and is hence neither even nor a multiple of three, so only odd numbers at least 5 can possibly be factors (besides 1).

This (along with 2, 3, and 5) accounts for all primes:
- Any number that *isn't* congruent to either 1 or 5 mod 6 has factors of 2 and/or 3 (because 2 and 4 mod 6 are even, 3 mod 6 is a multiple of three, and 0 mod 6 is both). Hence, any such number is not prime unless it's 2 or 3.
- The only primes that aren't greater than 5 are 2, 3, and 5.

# Experimental evaluation
I ran the program five times for each power of 10 up to the 10th, with 8 threads each time. Here's the results:

| Maximum | Count | Sum | Average runtime | Maximum runtime |
| --: | :--: | :--: | :-- | :-- |
| 1 | 0 | 0 | 3.31ms | 5.55ms |
| 10 | 4 | 17 | 5.11ms | 6.47ms |
| 100 | 25 | 1060 | 3.08ms | 3.79ms |
| 1000 | 168 | 76127 | 4.72ms | 11.46ms |
| 10000 | 1229 | 5736396 | 6.65ms | 8.23ms |
| 100000 | 9592 | 454396537 | 36.65ms | 63.12ms |
| 1000000 | 78498 | 37550402023 | 127.39ms | 178.13ms |
| 10000000 | 664579 | 3203324994356 | 961.87ms | 986.65ms |
| 100000000 | 5761455 | 279209790387276 | 20400.26ms | 20773.03ms |

Note that the counts and sums for powers of 10 can be validated against [https://oeis.org/A006880](A006880) and [https://oeis.org/A046731](A046731) in the OEIS.
