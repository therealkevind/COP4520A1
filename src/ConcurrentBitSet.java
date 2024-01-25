import java.util.BitSet;

/**
 * A small wrapper around a {@link BitSet}, allowing concurrently setting individual bits.
 */
public class ConcurrentBitSet {
  private final BitSet values;

  public ConcurrentBitSet() {
    values = new BitSet();
  }
  public ConcurrentBitSet(int nbits) {
    values = new BitSet(nbits);
  }

  public synchronized void set(int bitIndex) {
    values.set(bitIndex);
  }
  /**
   * Gets the underlying {@link BitSet}. This should not be read or modified while other threads may be calling {@link #set}.
   * @return The BitSet.
   */
  public synchronized BitSet getBitSet() {
    return values;
  }
}
