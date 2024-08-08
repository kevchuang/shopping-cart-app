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
      extends RefinedTypeOps[JwtSymmetricAuth, Pure, AdminJwtAuth]

  opaque type UserJwtAuth = JwtSymmetricAuth :| Pure
  object UserJwtAuth extends RefinedTypeOps[JwtSymmetricAuth, Pure, UserJwtAuth]

  final case class User(id: UserId, name: UserName) derives Show

  final case class UserWithPassword(
      id: UserId,
      name: UserName,
      password: EncryptedPassword
  ) derives Eq,
        Show

  opaque type AdminUser = User :| Pure
  object AdminUser extends RefinedTypeOps[User, Pure, AdminUser]

  opaque type CommonUser = User :| Pure
  object CommonUser extends RefinedTypeOps[User, Pure, CommonUser]
end users
