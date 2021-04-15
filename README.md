**This project will no longer be available after [Bintray shuts down on May 1, 2021](https://jfrog.com/blog/into-the-sunset-bintray-jcenter-gocenter-and-chartcenter/).**

If there is interest in getting it published to Maven Central, please open an issue.

# Akka Memory Usage Logger

[![license](https://img.shields.io/github/license/Dwolla/akka-memory-usage-logger.svg?style=flat-square)]()

Akka extension that logs the current values of `ManagementFactory.getMemoryPoolMXBeans` every 30 seconds.

If the extension is on the classpath, it will be enabled.

The polling period can be overridden by setting the config value `memoryUsageLogging.polling-period` to a positive integer, or disabled by setting it to `0`.
