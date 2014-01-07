package streaming

/** Helper to just add one to a function every time it's called. */
abstract class IncreasingCount {
  private val count = new java.util.concurrent.atomic.AtomicInteger()
  protected def nextCount: Int =
    count.addAndGet(1)
}