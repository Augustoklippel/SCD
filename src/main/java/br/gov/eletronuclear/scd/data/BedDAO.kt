package br.gov.eletronuclear.scd.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Query
import androidx.room3.Upsert

@Dao
interface BedDAO {

    @Query("SELECT * FROM bed")
    suspend fun getAll(): List<Bed>

    @Upsert
    suspend fun insert(user: Bed)

    @Delete
    suspend fun delete(user: Bed)

}
