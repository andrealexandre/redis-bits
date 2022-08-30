package org.aalexandre

import io.lettuce.core._
import io.lettuce.core.api.StatefulRedisConnection

import scala.util.Using

object Redis extends App {

  Using(RedisClient.create("redis://password@localhost:6379/0")) { redisClient =>
    Using(redisClient.connect()) { connection: StatefulRedisConnection[String, String] =>
      val syncCommand = connection.sync()
      syncCommand.set("key", "Hello, Redis!")
      val value = syncCommand.get("key")

      println(s"$value")
    }
  }

}
