package com.kevchuang.shop.config

import com.kevchuang.shop.domain.types.common.NotEmpty
import io.github.iltotore.iron.*

import scala.concurrent.duration.FiniteDuration

object types:
  opaque type JwtAccessTokenKeyConfig = String :| NotEmpty
  object JwtAccessTokenKeyConfig
      extends RefinedTypeOpsImpl[String, NotEmpty, JwtAccessTokenKeyConfig]

  opaque type PasswordSalt = String :| NotEmpty
  object PasswordSalt extends RefinedTypeOpsImpl[String, NotEmpty, PasswordSalt]

  opaque type TokenExpiration = FiniteDuration :| Pure
  object TokenExpiration
      extends RefinedTypeOpsImpl[FiniteDuration, Pure, TokenExpiration]
end types
