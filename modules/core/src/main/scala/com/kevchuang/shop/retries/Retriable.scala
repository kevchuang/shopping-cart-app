package com.kevchuang.shop.retries

import cats.Show
import cats.derived.*

sealed trait Retriable derives Show

object Retriable:
  case object Orders  extends Retriable
  case object Payment extends Retriable
end Retriable
