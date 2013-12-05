package capture

import rx.lang.scala._
import rx.lang.scala.concurrency._
import concurrent.duration._
import rx.concurrency.NewThreadScheduler

object Sync {
  
  
  
  val sixtyhertzInMillisTimeout = (1000L / 60L)-10
  println("milli delays: " + sixtyhertzInMillisTimeout)
  val collectionSynch = Observable.interval(sixtyhertzInMillisTimeout.millis)
}