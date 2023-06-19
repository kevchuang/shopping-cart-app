package com.kevchuang.shop.config

import ciris.Secret
import com.kevchuang.shop.config.PostgreSQLConfig.*
import eu.timepit.refined.types.string.NonEmptyString

final case class PostgreSQLConfig(
    host: HostName,
    port: PortNumber,
    user: UserName,
    password: Secret[NonEmptyString],
    database: DatabaseName,
    max: Int
)

object PostgreSQLConfig:
  opaque type HostName <: String = String
  object HostName:
    def apply(s: String): HostName = s

  opaque type PortNumber <: Int = Int
  object PortNumber:
    def apply(p: Int): PortNumber = p

  opaque type UserName <: String = String
  object UserName:
    def apply(s: String): UserName = s

  opaque type DatabaseName <: String = String
  object DatabaseName:
    def apply(s: String): DatabaseName = s
