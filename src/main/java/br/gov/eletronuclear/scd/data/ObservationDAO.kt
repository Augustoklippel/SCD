package br.gov.eletronuclear.scd.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query

@Dao
interface ObservationDAO {
    @Query("SELECT * FROM observation")
    suspend fun getAll(): List<Observation>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(observation: Observation)

    @Query("DELETE FROM observation")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(observations: List<Observation>)
}