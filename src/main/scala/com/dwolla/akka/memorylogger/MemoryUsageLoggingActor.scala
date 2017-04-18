package com.dwolla.akka.memorylogger

import java.lang.management.{ManagementFactory, MemoryPoolMXBean}

import akka.actor.{Actor, ActorLogging, Props}
import com.dwolla.akka.memorylogger.MemoryUsageLoggingActor.{MemoryPoolMX, WriteMemoryUsageToLog}
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.write

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.language.{implicitConversions, postfixOps}

class MemoryUsageLoggingActor(pollingPeriod: FiniteDuration) extends Actor with ActorLogging {
  implicit val formats = DefaultFormats
  import context.dispatcher
  lazy val mBeans: Seq[MemoryPoolMXBean] = ManagementFactory.getMemoryPoolMXBeans.asScala

  var memoryPoolMxs = Seq.empty[MemoryPoolMX]

  scheduleWriteMemoryUsageToLog()

  override def receive: Receive = {
    case WriteMemoryUsageToLog ⇒
      val updated: Seq[MemoryPoolMX] = mBeans.map(MemoryPoolMX.toCaseClass)

      if (!(memoryPoolMxs.length == updated.length && updated.forall(memoryPoolMxs.contains))) {
        log.info(write(updated.filterNot(memoryPoolMxs.contains)))
        memoryPoolMxs = updated
      }
  }

  protected def scheduleWriteMemoryUsageToLog(): Unit = context.system.scheduler.schedule(0 seconds, pollingPeriod, self, WriteMemoryUsageToLog)
}

object MemoryUsageLoggingActor {
  def apply(pollingPeriod: FiniteDuration): Props = Props(classOf[MemoryUsageLoggingActor], pollingPeriod)

  case object WriteMemoryUsageToLog

  private[memorylogger] case class MemoryPoolMX(name: String,
                                                `type`: String,
                                                usage: MemoryUsage)

  private[memorylogger] object MemoryPoolMX {
    implicit def toCaseClass(x: java.lang.management.MemoryPoolMXBean): MemoryPoolMX = MemoryPoolMX(x.getName, x.getType.toString, x.getUsage)
  }

  private[memorylogger] case class MemoryUsage(init: Long, used: Long, committed: Long, max: Long)

  private[memorylogger] object MemoryUsage {
    implicit def toCaseClass(x: java.lang.management.MemoryUsage): MemoryUsage = MemoryUsage(x.getInit, x.getUsed, x.getCommitted, x.getMax)
  }
}
