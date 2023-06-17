package com.kevchuang.shop.config

import ciris.Secret
import eu.timepit.refined.types.net.UserPortNumber
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString

final case class PostgreSQLConfig(
    host: NonEmptyString,
    port: UserPortNumber,
    user: NonEmptyString,
    password: Secret[NonEmptyString],
    database: NonEmptyString,
    max: PosInt
)
