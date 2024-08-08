package com.kevchuang.shop.suite

import cats.effect.*
import cats.syntax.flatMap.*
import weaver.IOSuite
import weaver.scalacheck.{CheckConfig, Checkers}

abstract class ResourceSuite extends IOSuite with Checkers:

  override def checkConfig: CheckConfig =
    CheckConfig.default.copy(minimumSuccessful = 1)

  extension (resource: Resource[IO, Res])
    def beforeAll(f: Res => IO[Unit]): Resource[IO, Res] =
      resource.evalTap(f)

    def afterAll(f: Res => IO[Unit]): Resource[IO, Res] =
      resource.flatTap(x => Resource.make(IO.unit)(_ => f(x)))
end ResourceSuite
