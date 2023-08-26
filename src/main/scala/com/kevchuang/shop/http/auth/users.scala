package com.kevchuang.shop.http.auth

import cats.*
import cats.derived.*
import com.kevchuang.shop.domain.auth.*
import dev.profunktor.auth.jwt.JwtSymmetricAuth
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given

object users:
  opaque type AdminJwtAuth = JwtSymmetricAuth :| Pure
  object AdminJwtAuth
      extends RefinedTypeOpsImpl[JwtSymmetricAuth, Pure, AdminJwtAuth]

  opaque type UserJwtAuth = JwtSymmetricAuth :| Pure
  object UserJwtAuth
      extends RefinedTypeOpsImpl[JwtSymmetricAuth, Pure, UserJwtAuth]

  final case class User(id: UserId, name: UserName) derives Show

  final case class UserWithPassword(
      id: UserId,
      name: UserName,
      password: EncryptedPassword
  ) derives Eq,
        Show
end users
