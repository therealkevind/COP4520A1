import java.util.concurrent.atomic.AtomicInteger;

class PrimeThread extends Thread {
  private final AtomicInteger candidate;
  private final ConcurrentBitSet primes;
  private final int max;
  private long sum = 0;

  PrimeThread(int max, AtomicInteger candidate, ConcurrentBitSet primes) {
    this.candidate = candidate;
    this.primes = primes;
    this.max = max;
  }

  public void run() {
    while (true) {
      // doing this in batches of 6 allows us to eliminate multiples of 2 and 3 automatically
      final int i = candidate.getAndAdd(6);
      if (i > max) return;
      doPrimeCheck(i + 1);
      doPrimeCheck(i + 5);
    }
  }
  /**
   * Performs a primality check, and sets the bit in {@link #primes} and adds to {@link #sum} as appropriate.
   * @param i The number to check for primality. Must not be divisible by two or three, and must be greater than one.
   */
  private void doPrimeCheck(int i) {
    if (i > max) return;
    if (isPrime(i)) {
      primes.set(i);
      sum += i;
    }
  }
  /**
   * Checks if a number is prime.
   * @param i The number to check for primality. Must not be divisible by two or three, and must be greater than one.
   * @return Whether the number is prime.
   * @implNote This performs trial division by <em>every</em> number up to the square root (until a divisor's found),
   *   because it's more efficient than trying to do a sieve and hence synchronizing on {@link #primes}.
   */
  private boolean isPrime(int i) {
    final int isqrt = (int)Math.sqrt(i);
    for (int p = 5; p <= isqrt; p += 2) {
      if (i % p == 0) {
        return false;
      }
    }
    return true;
  }
  /**
   * Gets the sum of the primes found by this thread.
   * @return The sum.
   */
  public long getSum() {
    return sum;
  }
}
