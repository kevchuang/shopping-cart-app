package com.kevchuang.shop.config

import com.kevchuang.shop.domain.types.common.NotEmpty
import io.github.iltotore.iron.*

import scala.concurrent.duration.FiniteDuration

object types:
  opaque type JwtAccessTokenKeyConfig = String :| NotEmpty
  object JwtAccessTokenKeyConfig
      extends RefinedTypeOps[String, NotEmpty, JwtAccessTokenKeyConfig]

  opaque type PasswordSalt = String :| NotEmpty
  object PasswordSalt extends RefinedTypeOps[String, NotEmpty, PasswordSalt]

  opaque type TokenExpiration = FiniteDuration :| Pure
  object TokenExpiration
      extends RefinedTypeOps[FiniteDuration, Pure, TokenExpiration]
end types
