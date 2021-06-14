package com.example.bioSpecies.data.entity

import java.util.*

class Especies (
    var nomeTradicional: String,
    var nomeCientifico: String,
    var localizacoesComuns: List<String>,
    var localizacoesFotos: List<Coordenadas>,
    var ordem: String,
    var familia: String,
    var habitat: String,
    var observacoes: String,
    var foto: Int,
    var raridade: Int
    ) {

        var uuid: String = UUID.randomUUID().toString()

    }