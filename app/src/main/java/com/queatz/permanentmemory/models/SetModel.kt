package com.queatz.permanentmemory.models

import io.objectbox.annotation.Entity

@Entity
data class SetModel(
        var name: String = "",
        var subject: Long = 0
) : BaseModel()