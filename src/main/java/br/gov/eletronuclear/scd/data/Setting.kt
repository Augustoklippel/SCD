package br.gov.eletronuclear.scd.data

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "setting")
data class Setting(
    @PrimaryKey @ColumnInfo(name = "col_number") val colNumber: String,
    @ColumnInfo(name = "user_id") val idUser: Int?,
    @ColumnInfo(name = "user_name") val userName: String?,
    @ColumnInfo(name = "area_id") val idArea: Int?,
    @ColumnInfo(name = "area_name") val areaName: String?,
    @ColumnInfo(name = "mac_address") val macAddress: String?
)