package com.kevchuang.shop.config

import ciris.Secret
import com.kevchuang.shop.config.PostgreSQLConfig.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final case class PostgreSQLConfig(
    host: HostName,
    port: PortNumber,
    user: UserName,
    password: Secret[Password],
    database: DatabaseName,
    max: Int
)

object PostgreSQLConfig:
  type HostName = String :| Pure
  object HostName extends RefinedTypeOps[HostName]

  type PortNumber = Int :| Positive
  object PortNumber extends RefinedTypeOps[PortNumber]

  type UserName = String :| Alphanumeric
  object UserName extends RefinedTypeOps[UserName]

  type Password = String :| MinLength[5]
  object Password extends RefinedTypeOps[Password]

  type DatabaseName = String :| Alphanumeric
  object DatabaseName extends RefinedTypeOps[DatabaseName]
end PostgreSQLConfig
