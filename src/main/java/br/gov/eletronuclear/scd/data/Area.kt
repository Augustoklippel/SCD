package br.gov.eletronuclear.scd.data

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "area")
data class Area(
    @PrimaryKey @ColumnInfo(name = "area_id") val idArea: Int = 0,
    @ColumnInfo(name = "area_name") val areaName: String
)
