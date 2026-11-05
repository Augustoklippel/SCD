package br.gov.eletronuclear.scd.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query

@Dao
interface AreaDAO {

    @Query("SELECT * FROM area")
    suspend fun getAll(): List<Area>

    @Insert
    suspend fun insertAll(vararg areas: Area)

    @Query("Delete FROM area")
    suspend fun deleteAll()
}