package com.dwolla.akka.memorylogger

import akka.actor.{ActorSystem, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}

class MemoryUsageLogger(system: ActorSystem) extends Extension {
  if (!system.settings.config.hasPath("memoryUsageLogging.testMode")) {
    system.actorOf(MemoryUsageLoggingActor(), "MemoryUsageLogger")
  }
}

object MemoryUsageLogger extends ExtensionId[MemoryUsageLogger] with ExtensionIdProvider {
  override def createExtension(system: ExtendedActorSystem): MemoryUsageLogger = new MemoryUsageLogger(system)

  override def lookup(): ExtensionId[_ <: Extension] = MemoryUsageLogger
}
