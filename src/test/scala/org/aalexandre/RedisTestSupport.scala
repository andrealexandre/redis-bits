package org.aalexandre

import akka.actor.ActorSystem
import com.typesafe.scalalogging.StrictLogging
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.codec.{RedisCodec, StringCodec}
import org.aalexandre.RedisTestSupport.{RedisLettuceFixture, RedisScalaFixture}
import org.scalatest.{BeforeAndAfterAll, Suite}
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Using

trait RedisTestSupport extends BeforeAndAfterAll with StrictLogging {
  suite: Suite =>

  private val redisContainer: GenericContainer[_] = new GenericContainer(DockerImageName.parse("redis:7.0.4"))
    .withExposedPorts(6379)
  private lazy val uri = s"redis://password@${redisContainer.getHost}:${redisContainer.getFirstMappedPort}/0"

  override protected def beforeAll(): Unit = redisContainer.start()
  override protected def afterAll(): Unit = redisContainer.stop()

  def withRedisLettuce[A, K, V](codec: RedisCodec[K, V])(fn: RedisLettuceFixture[K, V] => A): A = {
    Using(io.lettuce.core.RedisClient.create(uri)) { redisClient =>
      Using(redisClient.connect(codec)) { connection =>
        fn(RedisLettuceFixture(redisClient, connection))
      }
    }.flatten.fold(throwable => throw throwable, t => t)
  }

  def withRedisLettuce[A](fn: RedisLettuceFixture[String, String] => A): A = withRedisLettuce(StringCodec.UTF8)(fn)

  def withRedisScala[A](fn: RedisScalaFixture => A): A = {
    implicit val system: ActorSystem = ActorSystem()
    val redisClient = redis.RedisClient(host = redisContainer.getHost, port = redisContainer.getFirstMappedPort)
    fn(RedisScalaFixture(redisClient))
  }

}

object RedisTestSupport {
  case class RedisLettuceFixture[K, V](redisClient: io.lettuce.core.RedisClient, connection: StatefulRedisConnection[K, V])
  case class RedisScalaFixture(client: redis.RedisClient)
}
