package br.gov.eletronuclear.scd.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Upsert

@Dao
interface UserDAO{
    @Query("SELECT * FROM user")
    suspend fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE user_id IN (:userId)")
    suspend fun findById(userId: Int): User

    @Query(
        """
        SELECT * FROM user
        WHERE number LIKE :number LIMIT 1
        """
    )
    suspend fun findByNumber(number: String): User?

    @Query(
        """
        SELECT * FROM user
        WHERE user_name LIKE :name LIMIT 1
        """
    )
    suspend fun findByName(name: String): User?

    @Insert
    suspend fun insertAll(vararg users: User)

    @Upsert
    suspend fun insert(user: User)

    @Delete
    suspend fun delete(user: User)

}
