package dev.patrickgold.florisboard.ime.suggesstions.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "suggesstion_table")
class SuggesstionEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    var suggesstionId: Int = 0,
    var word: String? = null
// var hint: String? = null,
// @ColumnInfo(name = "favorite", defaultValue = "0")
// var favourite: Int? = 0,
// @ColumnInfo(name = "egrammar", defaultValue = "null")
// var grammer: String? = null,
// var recent: String? = null,
// @ColumnInfo(name = "recentwords", defaultValue = "0")
// var recentWords: Int? = 0
)