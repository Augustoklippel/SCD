package br.gov.eletronuclear.scd.data

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "observation")
data class Observation(
    @PrimaryKey @ColumnInfo(name = "number") @SerializedName("nu_observacao") val number: Int,
    @ColumnInfo(name = "reference") @SerializedName("referencia") val reference: Int,
    @ColumnInfo(name = "code") @SerializedName("codigo") val code: Int,
    @ColumnInfo(name = "description") @SerializedName("descricao") val description: String,
)
