package com.kevchuang.shop.domain.types

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

object common:

  type NotEmpty = Not[Empty]

end common
