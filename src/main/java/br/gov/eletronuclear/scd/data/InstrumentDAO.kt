package br.gov.eletronuclear.scd.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Upsert

@Dao
interface InstrumentDAO {

    @Query("SELECT * FROM instrument")
    suspend fun getAll(): List<Instrument>

    @Upsert
    suspend fun insert(instrument: Instrument)

    @Delete
    suspend fun delete(instrument: Instrument)

    @Query("DELETE FROM instrument")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(instruments: List<Instrument>)

}