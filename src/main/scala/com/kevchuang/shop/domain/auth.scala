package com.kevchuang.shop.domain

import com.kevchuang.shop.domain.types.common.NotEmpty

import java.util.UUID
import io.github.iltotore.iron.*

import scala.util.control.NoStackTrace

object auth:
  opaque type UserId = UUID :| Pure
  object UserId extends RefinedTypeOpsImpl[UUID, Pure, UserId]

  opaque type UserName = String :| NotEmpty
  object UserName extends RefinedTypeOpsImpl[String, NotEmpty, UserName]

  opaque type Password = String :| NotEmpty
  object Password extends RefinedTypeOpsImpl[String, NotEmpty, Password]

  opaque type EncryptedPassword = String :| NotEmpty
  object EncryptedPassword
      extends RefinedTypeOpsImpl[String, NotEmpty, EncryptedPassword]

  opaque type UserNameParam = String :| NotEmpty
  object UserNameParam
      extends RefinedTypeOpsImpl[String, NotEmpty, UserNameParam]:
    extension (userNameParam: UserNameParam)
      def toDomain: UserName = UserName(userNameParam.value)

  opaque type PasswordParam = String :| NotEmpty
  object PasswordParam
      extends RefinedTypeOpsImpl[String, NotEmpty, PasswordParam]:
    extension (passwordParam: PasswordParam)
      def toDomain: Password = Password(passwordParam.value)

  final case class CreateUser(
      userName: UserNameParam,
      password: PasswordParam
  )

  final case class LoginUser(
      userName: UserNameParam,
      password: PasswordParam
  )

  final case class UserNotFound(userName: UserName)    extends NoStackTrace
  final case class UserNameInUse(userName: UserName)   extends NoStackTrace
  final case class InvalidPassword(userName: UserName) extends NoStackTrace
end auth
