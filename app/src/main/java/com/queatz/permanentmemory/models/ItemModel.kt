package com.queatz.permanentmemory.models

import io.objectbox.annotation.Entity

@Entity
data class ItemModel(
        var set: Long = 0,
        var score: Long = 0,
        var question: String = "",
        var answer: String = ""
) : BaseModel()