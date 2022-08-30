package org.aalexandre

import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.have
import org.scalatest.matchers.should
import redis.RedisClient
import redis.commands.TransactionBuilder
import redis.protocol.RedisReply

class RedisTest extends AnyFlatSpec with should.Matchers with RedisTestSupport {

  "The redis lettuce connection" should "be able to clear, read, write and read key-value" in
  withRedisLettuce { fixture =>
    val key = "test0"
    val value = "value0"
    val cmd = fixture.connection.sync()
    cmd.del(key)
    cmd.get(key) should be(null)
    cmd.set(key, value) should be("OK")
    cmd.get(key) should be(value)
  }

  "The redis scala connection" should "be able to clear, read, write and read key-value" in
  withRedisScala { fixture =>
    val key = "test0"
    val value = "value0"

    val client: RedisClient = fixture.client
    client.del(key).futureValue
    client.get[String](key).futureValue should be(None)
    client.set(key, value).futureValue should be(true)
    client.get[String](key).futureValue should be(Some(value))
  }

  "The redis scala transaction" should "be able to clear, read, write and read key-value" in
  withRedisScala { fixture =>
    val key = "test0"
    val value = "value0"

    val transaction: TransactionBuilder = fixture.client.transaction()
    transaction.watch(key)
    val del1 = transaction.del(key)
    val get1 = transaction.get[String](key)
    val set1 = transaction.set(key, value)
    val get2 = transaction.get[String](key)
    val exec = transaction.exec().futureValue

    get1.futureValue should be(None)
    set1.futureValue should be(true)
    get2.futureValue should be(Some(value))
  }

}
