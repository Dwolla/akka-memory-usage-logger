package com.dwolla.akka.memorylogger

import java.lang.management.MemoryType.HEAP
import java.lang.management.{MemoryPoolMXBean, MemoryType}

import akka.actor.Props
import akka.testkit.EventFilter.info
import akka.testkit.TestActorRef
import com.dwolla.akka.memorylogger.MemoryUsageLoggingActor.{MemoryPoolMX, MemoryUsage, WriteMemoryUsageToLog}
import com.dwolla.testutils.akka.AkkaTestKitSpecs2Support
import com.dwolla.testutils.concurrency.PromiseMatchers.beCompleted
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

import scala.concurrent.Promise
import scala.concurrent.duration._

class MemoryLoggingActorSpec(implicit ee: ExecutionEnv) extends Specification with Mockito {

  trait Setup extends AkkaTestKitSpecs2Support {
    val mockMxBean1 = mock[MemoryPoolMXBean]
    val mockMxBean2 = mock[MemoryPoolMXBean]
    val mockMxBeans = Seq(mockMxBean1, mockMxBean2)

    val promisedScheduleWriteMemoryUsageToLog = Promise[Unit]

    class MockedMemoryLoggingActor extends MemoryUsageLoggingActor(5 second span) {
      override lazy val mBeans: Seq[MemoryPoolMXBean] = mockMxBeans

      override protected def scheduleWriteMemoryUsageToLog(): Unit = promisedScheduleWriteMemoryUsageToLog.success(())
    }
  }

  "MemoryLoggingActor" should {
    "schedule WriteMemoryUsageToLog" in new Setup {
      val actor = TestActorRef(Props(new MockedMemoryLoggingActor))

      promisedScheduleWriteMemoryUsageToLog must beCompleted
    }

    "write to the debug log when receiving WriteMemoryUsageToLog" in new Setup {
      mockMxBeans.foreach(defineBean(_))

      val actor = TestActorRef(Props(new MockedMemoryLoggingActor))

      info(message = """[{"name":"bean","type":"Heap memory","usage":{"init":0,"used":1,"committed":2,"max":3}},{"name":"bean","type":"Heap memory","usage":{"init":0,"used":1,"committed":2,"max":3}}]""", occurrences = 1) intercept {
        actor ! WriteMemoryUsageToLog
      }
    }

    "only log changes" in new Setup {
      val actor = TestActorRef(Props(new MockedMemoryLoggingActor {
        memoryPoolMxs = Seq(MemoryPoolMX("bean", HEAP.toString, MemoryUsage(0L, 1L, 2L, 3L)), MemoryPoolMX("bean", HEAP.toString, MemoryUsage(0L, 1L, 5L, 6L)))
      }))

      defineBean(mockMxBean1)
      defineBean(mockMxBean2, usage = new java.lang.management.MemoryUsage(0L, 4L, 5L, 6L))

      info(message = """[{"name":"bean","type":"Heap memory","usage":{"init":0,"used":4,"committed":5,"max":6}}]""", occurrences = 1) intercept {
        actor ! WriteMemoryUsageToLog
      }
    }
  }

  def defineBean(bean: MemoryPoolMXBean, name: String = "bean", memoryType: MemoryType = HEAP, usage: java.lang.management.MemoryUsage = new java.lang.management.MemoryUsage(0L, 1L, 2L, 3L)): Unit = {
    bean.getName returns name
    bean.getType returns memoryType
    bean.getUsage returns usage
  }
}
