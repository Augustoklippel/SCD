package br.gov.eletronuclear.scd.data

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "justification")
data class Justification(
    @PrimaryKey @ColumnInfo(name = "code") @SerializedName("codigo") val code: String,
    @ColumnInfo(name = "description") @SerializedName("descricao") val description: String,
)
