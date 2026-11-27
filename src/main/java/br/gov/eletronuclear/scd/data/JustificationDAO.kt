package br.gov.eletronuclear.scd.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Upsert

@Dao
interface JustificationDAO {
    @Query("SELECT * FROM justification")
    suspend fun getAll(): List<Justification>

    @Upsert
    suspend fun insert(justification: Justification)

    @Delete
    suspend fun delete(justification: Justification)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(justifications: List<Justification>)
}