package br.gov.eletronuclear.scd.data

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey @ColumnInfo(name = "user_id") var idUser: Int = 0,
    @ColumnInfo(name = "user_name") var userName: String,
    @ColumnInfo(name = "number") var number: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "type") val type: Int
)
