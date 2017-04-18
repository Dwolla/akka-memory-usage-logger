# Akka Memory Usage Logger

[![Travis](https://img.shields.io/travis/Dwolla/akka-memory-usage-logger.svg?style=flat-square)](https://travis-ci.org/Dwolla/akka-memory-usage-logger)
[![Bintray](https://img.shields.io/bintray/v/dwolla/maven/akka-memory-usage-logger.svg?style=flat-square)](https://bintray.com/dwolla/maven/akka-memory-usage-logger/view)
[![license](https://img.shields.io/github/license/Dwolla/akka-memory-usage-logger.svg?style=flat-square)]()

Akka extension that logs the current values of `ManagementFactory.getMemoryPoolMXBeans` every 30 seconds.

If the extension is on the classpath, it will be enabled.

The polling period can be overridden by setting the config value `memoryUsageLogging.polling-period` to a positive integer, or disabled by setting it to `0`.
