package com.example.bioSpecies.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.bioSpecies.data.local.room.DataConverter
import com.example.bioSpecies.data.remote.responses.ObservationSpeciesResponse
import java.util.*

@Entity
class UserAnimal (
    var nomeTradicional: String,
    var descoberto: Boolean
    ) {

    @PrimaryKey
    var uuid: String = UUID.randomUUID().toString()

}