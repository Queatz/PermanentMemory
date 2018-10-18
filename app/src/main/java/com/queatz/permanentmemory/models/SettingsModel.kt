package com.queatz.permanentmemory.models

import io.objectbox.annotation.Entity
import java.util.*

@Entity
data class SettingsModel(
        var isInitialSubjectLoaded: Boolean = false,
        var wordOfTheDay: Long? = null,
        var wordOfTheDayDate: Date? = null
) : BaseModel()
