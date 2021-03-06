package com.queatz.permanentmemory.models

import io.objectbox.annotation.BaseEntity
import io.objectbox.annotation.Id
import java.util.*

@BaseEntity
open class BaseModel(
        @Id(assignable = true) var objectBoxId: Long = 0,
        var created: Date = Date(),
        var updated: Date = Date()
)