package com.nnjtrading.gamez

class Question: java.io.Serializable {

    private var country: String?
    private var countryCode: String

    constructor (country: String?, countryCode: String) {
        this.country = country
        this.countryCode = countryCode
    }

    fun getFlag(): String {
        return this.countryCode
    }

    fun getCountry(): String? {
        return this.country
    }
}