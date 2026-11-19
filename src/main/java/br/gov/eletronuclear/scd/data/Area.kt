package br.gov.eletronuclear.scd.data

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "area")
data class Area(
    @PrimaryKey @ColumnInfo(name = "area_id") @SerializedName("codigo") val idArea: String,
    @ColumnInfo(name = "area_name") @SerializedName("descricao") val areaName: String
)
