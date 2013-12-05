

import java.util.concurrent.ConcurrentLinkedQueue

/**
 * This is an execution context that has its own thread and waits N millis (minimum)
 * before running more code.
 */
class SyncExecutionContext(ms: Long) extends concurrent.ExecutionContext {
  
  private val queue = new ConcurrentLinkedQueue[Runnable]()
  @volatile private var running = true
  
  private class MyThread extends Thread {
    @volatile var lastFrameTime = 0L
    def ensureWait(): Unit = {
      var time = System.currentTimeMillis();
      while (time - lastFrameTime < (ms - 10)) {
        // TODO - Ignore interrupted exceptions?
        Thread.sleep(10);
        time = System.currentTimeMillis();
      }
      lastFrameTime = time
    }
    override def run(): Unit =  {
      while(running) {
        ensureWait()
        val next = queue.poll()
        if(next != null) next.run()
        else {
          Thread.sleep(10)
        }
      }
    }
  }
  
  private val runner = {
    val t = new MyThread
    t.start()
    t
  }
  def execute(r: Runnable): Unit = {
    queue.add(r)
  }
  // TODO - kill our thread?
  def reportFailure(t: Throwable): Unit = ()
  
}