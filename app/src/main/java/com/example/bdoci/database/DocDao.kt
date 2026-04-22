package com.example.bdoci.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bdoci.models.Doc

@Dao
interface DocDao {
    @Query("SELECT * FROM documents")
    suspend fun getAllDocs(): List<Doc>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(docs: List<Doc>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoc(doc: Doc)

    @Query("DELETE FROM documents")
    suspend fun deleteAll()
}