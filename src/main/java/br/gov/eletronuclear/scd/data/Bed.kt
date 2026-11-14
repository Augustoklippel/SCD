package br.gov.eletronuclear.scd.data
import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "bed")
data class Bed(
    @PrimaryKey @ColumnInfo(name = "bed_id") var idBed: Int = 0,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "mac_address") var macAddress: String,
    @ColumnInfo(name = "local") val local: String,

)
