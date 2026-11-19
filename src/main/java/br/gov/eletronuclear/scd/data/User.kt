package br.gov.eletronuclear.scd.data

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.google.gson.annotations.SerializedName
@Entity(tableName = "user")
data class User(
    //@ColumnInfo(name = "user_id") var idUser: Int = 0,
    @ColumnInfo(name = "user_name") @SerializedName("nome") var userName: String,
    @PrimaryKey @ColumnInfo(name = "number") @SerializedName("matricula") var number: String,
    @ColumnInfo(name = "password") @SerializedName("senha") val password: String,
    @ColumnInfo(name = "type") @SerializedName("isadm") val type: Int
)
