package com.nnjtrading.gamez

import android.content.Context
import java.io.InputStream

class Countries(private var context: Context) {

    fun countriesMap(): Map<String, String> {

        var countries: String? = null

        var countryCodesMap: MutableMap<String, String> = mutableMapOf()

        try{
            var inputStream: InputStream = context.assets.open("countries.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            countries = String(buffer, Charsets.UTF_8)


            var stringList = countries.split("\n")

            for(i in 0 until stringList.size ) {
                var line = stringList.get(i)
                if(!line.equals("{") && !line.equals("}") && !line.contains("{") && !line.contains("}")) {
                    var key = line.split(":").get(0).replace(" ", "")
                    var value = line.split(":").get(1).replace(",", "")
                    value = value.replace("\"", "")
                    key = key.replace("\"", "")
                    countryCodesMap.put(key, value.substring(1))
                }
            }

        } catch (e: Exception) {
        }

        return countryCodesMap
    }

    fun countriesCodeList(): List<String> {
        return countriesMap().keys.toList()
    }

    fun countriesList(): List<String> {
        return countriesMap().values.toList()
    }
}

