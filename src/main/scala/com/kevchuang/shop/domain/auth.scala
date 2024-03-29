package com.kevchuang.shop.domain

import com.kevchuang.shop.domain.types.common.NotEmpty
import dev.profunktor.auth.jwt.JwtToken
import io.circe.*
import cats.*
import io.github.iltotore.iron.*

import java.util.UUID
import javax.crypto.Cipher
import scala.util.control.NoStackTrace

object auth:
  opaque type UserId = UUID :| Pure
  object UserId extends RefinedTypeOpsImpl[UUID, Pure, UserId]

  opaque type UserName = String :| NotEmpty
  object UserName extends RefinedTypeOpsImpl[String, NotEmpty, UserName]

  opaque type Password = String :| NotEmpty
  object Password extends RefinedTypeOpsImpl[String, NotEmpty, Password]

  opaque type EncryptCipher = Cipher :| Pure
  object EncryptCipher extends RefinedTypeOpsImpl[Cipher, Pure, EncryptCipher]

  opaque type DecryptCipher = Cipher :| Pure
  object DecryptCipher extends RefinedTypeOpsImpl[Cipher, Pure, DecryptCipher]

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

  given Encoder[JwtToken] = Encoder.forProduct1("access_token")(_.value)
  given Eq[JwtToken]      = Eq.by(_.value)
end auth
