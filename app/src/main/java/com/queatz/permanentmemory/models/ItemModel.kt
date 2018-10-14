package com.queatz.permanentmemory.models

data class ItemModel(
        var set: Long,
        var score: Long,
        var question: String,
        var answer: String
) : BaseModel()