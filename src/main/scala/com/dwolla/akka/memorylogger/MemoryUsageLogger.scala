package com.dwolla.akka.memorylogger

import akka.actor.{ActorSystem, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}

import scala.concurrent.duration._
import scala.language.postfixOps

class MemoryUsageLogger(system: ActorSystem) extends Extension {
  private val pollingPeriod = system.settings.config.getInt("memoryUsageLogging.polling-period")

  if (pollingPeriod > 0) {
    system.actorOf(MemoryUsageLoggingActor(pollingPeriod seconds), "MemoryUsageLogger")
  }
}

object MemoryUsageLogger extends ExtensionId[MemoryUsageLogger] with ExtensionIdProvider {
  override def createExtension(system: ExtendedActorSystem): MemoryUsageLogger = new MemoryUsageLogger(system)

  override def lookup(): ExtensionId[_ <: Extension] = MemoryUsageLogger
}
