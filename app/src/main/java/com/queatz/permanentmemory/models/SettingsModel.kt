package com.queatz.permanentmemory.models

import com.queatz.permanentmemory.logic.PlayMode
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.converter.PropertyConverter
import java.util.*

@Entity
data class SettingsModel(
        var isInitialSubjectLoaded: Boolean = false,
        var wordOfTheDay: Long? = null,
        var wordOfTheDayDate: Date? = null,
        @Convert(converter = PlayModeConverter::class, dbType = String::class)
        var playMode: PlayMode = PlayMode.RANDOM
) : BaseModel()

class PlayModeConverter : PropertyConverter<PlayMode, String> {
    override fun convertToDatabaseValue(entityProperty: PlayMode?) = entityProperty?.toString()
    override fun convertToEntityProperty(databaseValue: String?) = databaseValue?.let { PlayMode.valueOf(it) } ?: PlayMode.RANDOM
}
