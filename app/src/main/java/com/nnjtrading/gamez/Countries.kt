package com.nnjtrading.gamez

import android.content.Context
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/* References for out sources referred to achieve functions of this class */

// Gomez, O. de la H. (2023). How to read a JSON file from the assets folder using Kotlin. [online].
// Available at: https://www.delasign.com/blog/android-studio-kotlin-read-json/ [Accessed 2 Apr. 2024].

class Countries(private var context: Context) {

    // function to read countries JSON file and retrieve key value pairs and add to kotlin MAP
    fun countriesMap(): Map<String, String> {

        val countryCodesMap: MutableMap<String, String> = mutableMapOf()

        try{
            // reading JSON file as a String
            // reference: How to read a JSON file from the assets folder using Kotlin available at https://www.delasign.com/blog/android-studio-kotlin-read-json/
            val countriesJSONFile = context.assets.open("countries.json")
            val bufferReader = BufferedReader(InputStreamReader(countriesJSONFile))

            bufferReader.useLines {lines ->
                lines.forEach {
                    if(!it.equals("{") && !it.equals("}") && !it.contains("{") && !it.contains("}")) {
                        var key = it.split(":").get(0).replace(" ", "")
                        var value = it.split(":").get(1).replace(",", "")
                        value = value.replace("\"", "")
                        key = key.replace("\"", "")
                        countryCodesMap.put(key, value.substring(1))
                    }
                }
            }

        } catch (e: Exception) {
            println("Error reading JSON file")
        }

        return countryCodesMap
    }

    // function to return a list of country codes
    fun countriesCodeList(): List<String> {
        return countriesMap().keys.toList()
    }

    // function to return a list of country names
    fun countriesList(): List<String> {
        return countriesMap().values.toList()
    }
}

