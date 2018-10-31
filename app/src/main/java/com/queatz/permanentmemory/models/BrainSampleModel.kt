package com.queatz.permanentmemory.models

import io.objectbox.annotation.Entity

@Entity
data class BrainSampleModel(
        var set: Long = 0,
        var item: Long = 0,
        var correct: Boolean = false,
        var inverse: Boolean = false
) : BaseModel()