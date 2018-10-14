package com.queatz.permanentmemory.models

data class BrainSampleModel(
        var set: Long,
        var item: Long,
        var correct: Boolean
) : BaseModel()