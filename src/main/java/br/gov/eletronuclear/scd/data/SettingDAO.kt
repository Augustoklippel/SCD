package br.gov.eletronuclear.scd.data

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import androidx.room3.Update

@Dao
interface SettingDAO {

    @Query("SELECT * FROM setting LIMIT 1")
    suspend fun getSetting(): Setting?

    @Upsert
    suspend fun upsertSetting(setting: Setting)

    @Query("UPDATE setting SET user_name = :newUserName, user_id = :userId")
    suspend fun updateUserLogged(userId: Int, newUserName: String): Int

    @Query("UPDATE setting SET col_number = :colNumber")
    suspend fun updateColNumber(colNumber: String): Int

}