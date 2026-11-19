package br.gov.eletronuclear.scd.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query

@Dao
interface AreaDAO {

    @Query("SELECT * FROM area")
    suspend fun getAll(): List<Area>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(areas: List<Area>)

    @Query("Delete FROM area")
    suspend fun deleteAll()
}