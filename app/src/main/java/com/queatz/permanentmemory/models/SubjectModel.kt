package com.queatz.permanentmemory.models

import io.objectbox.annotation.Entity

@Entity
data class SubjectModel(
        var name: String = "",
        var inverse: String = ""
) : BaseModel()
