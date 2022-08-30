package org.aalexandre

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class RedisTest extends AnyFlatSpec with should.Matchers with RedisTestSupport {

  "The redis connection" should "be able to clear, read, write and read key-value" in
  withRedis { fixture =>
    val key = "test0"
    val value = "value0"
    val cmd = fixture.connection.sync()
    cmd.del(key)
    cmd.get(key) should be(null)
    cmd.set(key, value) should be("OK")
    cmd.get(key) should be(value)
  }

}
