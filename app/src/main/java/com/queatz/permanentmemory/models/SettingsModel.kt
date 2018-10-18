package com.queatz.permanentmemory.models

import io.objectbox.annotation.Entity

@Entity
data class SettingsModel(
        var isInitialSubjectLoaded: Boolean = false
) : BaseModel()
