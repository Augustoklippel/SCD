package br.gov.eletronuclear.scd.data
import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "bed")
data class Bed(
    //@PrimaryKey @ColumnInfo(name = "bed_id") var idBed: Int = 0,
    @ColumnInfo(name = "description") @SerializedName("descricao") var description: String,
    @PrimaryKey @ColumnInfo(name = "mac_address") @SerializedName("mac") var macAddress: String,
    @ColumnInfo(name = "local") @SerializedName("localizacao") val local: String,

)
