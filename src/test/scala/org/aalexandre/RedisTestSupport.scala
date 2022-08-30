package org.aalexandre

import com.typesafe.scalalogging.StrictLogging
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.codec.{RedisCodec, StringCodec}
import org.aalexandre.RedisTestSupport.RedisFixture
import org.scalatest.{BeforeAndAfterAll, Suite}
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

import scala.util.Using

trait RedisTestSupport extends BeforeAndAfterAll with StrictLogging {
  suite: Suite =>

  private val redis: GenericContainer[_] = new GenericContainer(DockerImageName.parse("redis:7.0.4"))
    .withExposedPorts(6379)
  private lazy val uri = s"redis://password@${redis.getHost}:${redis.getFirstMappedPort}/0"

  override protected def beforeAll(): Unit = redis.start()
  override protected def afterAll(): Unit = redis.stop()

  def withRedis[A, K, V](codec: RedisCodec[K, V])(fn: RedisFixture[K, V] => A): A = {
    Using(RedisClient.create(uri)) { redisClient =>
      Using(redisClient.connect(codec)) { connection =>
        fn(RedisFixture(redisClient, connection))
      }
    }.flatten.fold(throwable => throw throwable, t => t)
  }

  def withRedis[A](fn: RedisFixture[String, String] => A): A = withRedis(StringCodec.UTF8)(fn)

}

object RedisTestSupport {
  case class RedisFixture[K, V](redisClient: RedisClient, connection: StatefulRedisConnection[K, V])
}
