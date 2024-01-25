import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;

public class App {

  public static void main(String[] args) throws InterruptedException {
    // parse command line arguments
    if (args.length > 4) {
      System.err.println("Expected up to four arguments, got " + args.length);
      System.exit(1);
      return;
    }
    final int max = args.length > 0 ? parseInt(args[0]) : 100000000,
      threadCount = args.length > 1 ? parseInt(args[1]) : 8,
      numTopPrimes = args.length > 2 ? parseInt(args[2]) : 10;
    final String filename = args.length > 3 ? args[3] : "primes.txt";

    // initialize data
    final AtomicInteger candidate = new AtomicInteger(6);
    final ConcurrentBitSet primes = new ConcurrentBitSet(max);
    if (max >= 2) primes.set(2);
    if (max >= 3) primes.set(3);
    if (max >= 5) primes.set(5);
    
    // spawn the threads
    final long startTime = System.nanoTime();
    PrimeThread[] threads = new PrimeThread[threadCount];
    for (int i = 0; i < threadCount; i++) {
      threads[i] = new PrimeThread(max, candidate, primes);
      threads[i].start();
    }
    for (PrimeThread thread : threads) {
      thread.join();
    }
    final long endTime = System.nanoTime();
    // primes (and partial sums) have been found

    // do some wrapping up
    long sum = max >= 5 ? 10 : max >= 3 ? 5 : max >= 2 ? 2 : 0;
    for (PrimeThread thread : threads) {
      sum += thread.getSum();
    }
    final BitSet bs = primes.getBitSet();
    final int[] topPrimes = new int[numTopPrimes];
    for (int i = numTopPrimes, p = max+1; i-- > 0; ) {
      p = bs.previousSetBit(p - 1);
      if (p >= 0) topPrimes[i] = p;
      else break;
    }
    final int numPrimes = bs.cardinality();

    // outputs
    if (filename.equals("-")) {
      printInfo(new PrintWriter(System.out, true), endTime - startTime, numPrimes, sum, topPrimes);
    } else {
      try (FileWriter fw = new FileWriter(filename);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw, true)) {
        printInfo(pw, endTime - startTime, numPrimes, sum, topPrimes);
      } catch (IOException e) {
        System.err.println("Couldn't write to primes.txt:");
        e.printStackTrace();
        System.exit(1);
      }
    }
  }
  /**
   * Writes the output in the specification's format.
   * @param pw The writer to output with.
   * @param nanoTime The time elapsed, in nanoseconds. (Approximately.)
   * @param numPrimes The number of primes found.
   * @param sum The sum of primes found.
   * @param topPrimes The top few primes. If there are less primes than the number requested, fill the remainder with zeroes.
   */
  private static void printInfo(PrintWriter pw, long nanoTime, int numPrimes, long sum, int[] topPrimes) {
    pw.printf("%.2fms %d %d\n", nanoTime / 1000000., numPrimes, sum);
    for (int p : topPrimes) {
      if (p > 0) {
        pw.print(p);
        pw.print(' ');
      }
    }
    pw.println();
  }
  private static int parseInt(String arg) {
    final int x;
    try {
      x = Integer.parseInt(arg);
    } catch (NumberFormatException e) {
      System.err.println("Not a valid integer.");
      System.exit(1);
      return -1;
    }
    if (x <= 0) {
      System.err.println("Expected a positive integer.");
      System.exit(1);
    }
    return x;
  }
}
