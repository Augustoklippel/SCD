package br.gov.eletronuclear.scd.data

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "instrument")
data class Instrument(
    @PrimaryKey @ColumnInfo(name = "code") @SerializedName("codigo") val code: String,
    @ColumnInfo(name = "area_id") @SerializedName("area") val areaId: String,
    @ColumnInfo(name = "subarea_id") @SerializedName("subarea") val subAreaId: String,
    @ColumnInfo(name = "description") @SerializedName("descricao") val description: String,
    @ColumnInfo(name = "number") @SerializedName("numero") val number: String,
    @ColumnInfo(name = "val_max") @SerializedName("maximo") val valMax: String,
    @ColumnInfo(name = "val_min") @SerializedName("minimo") val valMin: String,
    @ColumnInfo(name = "proccess_code") @SerializedName("processamento") val processCode: String,
    @ColumnInfo(name = "time_register") @SerializedName("horarioleitura") val timeRegister: String
)
