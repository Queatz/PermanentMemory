package com.queatz.permanentmemory.models

import io.objectbox.annotation.Entity
import java.util.*

@Entity
data class ItemModel(
        var set: Long = 0,
        var score: Long = 0,
        var question: String = "",
        var answer: String = "",
        var streak: Long = 0,
        var reviewed: Date = Date(0)
) : BaseModel()