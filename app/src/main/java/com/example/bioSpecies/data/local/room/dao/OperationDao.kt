package com.example.bioSpecies.data.local.room.dao

import androidx.room.*
import com.example.bioSpecies.data.entity.Animal
import com.example.bioSpecies.data.entity.EstatisticasDesafios
import com.example.bioSpecies.data.entity.Semana
import retrofit2.http.GET


@Dao
interface OperationDao {

    //Animal

    @Insert
    suspend fun insertAnimal(animal: Animal)

    @Update
    suspend fun updateAnimal(animal: Animal)

    @Delete
    suspend fun deleteAnimal(animal: Animal)

    @Query("SELECT * FROM animal")
    suspend fun getAllAnimal(): List<Animal>

    @Query("SELECT * FROM animal WHERE uuid - :uuid")
    suspend fun getAnimalById(uuid: String): Animal


    //Semana

    @Insert
    suspend fun insertSemana(semana: Semana)

    @Update
    suspend fun updateSemana(semana: Semana)

    @Delete
    suspend fun deleteSemana(semana: Semana)

    @Query("SELECT * FROM semana")
    suspend fun getAllSemanas(): List<Semana>

    //EstatisticasDesafios

    @Insert
    suspend fun insertEstatisticasDesafios(estatisticasDesafios: EstatisticasDesafios)

    @Update
    suspend fun updateEstatisticasDesafios(estatisticasDesafios: EstatisticasDesafios)

    @Delete
    suspend fun deleteEstatisticasDesafios(estatisticasDesafios: EstatisticasDesafios)

    @Query("SELECT * FROM estatisticasdesafios")
    suspend fun getAllEstatisticasDesafios(): List<EstatisticasDesafios>

    //First time on the app
    /*@Query("SELECT * FROM firstTime")
    suspend fun getFistTime(): Boolean

    @Insert
    suspend fun insertFirstTime(firstTime: Boolean)

    @Update
    suspend fun updateFirstTime(firstTime: Boolean)*/

}