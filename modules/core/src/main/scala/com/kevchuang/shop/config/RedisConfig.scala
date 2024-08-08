package com.kevchuang.shop.config

import com.kevchuang.shop.config.RedisConfig.RedisURI
import com.kevchuang.shop.domain.types.common.NotEmpty
import io.github.iltotore.iron.*

final case class RedisConfig(uri: RedisURI)

object RedisConfig:
  opaque type RedisURI = String :| NotEmpty
  object RedisURI extends RefinedTypeOps[String, NotEmpty, RedisURI]
end RedisConfig
