package com.nnjtrading.gamez

import android.content.Context
import java.util.*

/* References for out sources referred to achieve functions of the application */

// Stack Overflow. (n.d.). how to access the drawable resources by name in android. [online]
// Available at: https://stackoverflow.com/questions/16369814/how-to-access-the-drawable-resources-by-name-in-android [Accessed 2 Apr. 2024].



// kotlin file consisting common functions used across the application

// function to get drawable image file using country code
// reference: how to access the drawable resources by name in android available at https://stackoverflow.com/questions/16369814/how-to-access-the-drawable-resources-by-name-in-android
fun getCountryFlagResourceId(context: Context, countryCode: String,): Int {
    return context.resources.getIdentifier(countryCode, "drawable", context.packageName)
}

// function to generate random question with a country code and country name
fun generateRandomFlag(context: Context): Question {

    // countries kotlin class that returns a kotlin Map of country codes and country names
    val countries = Countries(context)

    val countryCodesMap = countries.countriesMap()

    val keysList = countries.countriesCodeList()

    val RandomNumberGenerator = Random()

    val randomNumber = RandomNumberGenerator.nextInt(255);

    var flag = keysList.get(randomNumber)

    val country = countryCodesMap.get(flag)

    // replacing middle slashes with underscore to resolve android naming error in drawable
    if(flag.equals("DO")) {
        flag = "DO_FLAG"
    } else if(flag.equals("GB-ENG")) {
        flag = "GB_ENG"
    } else if(flag.equals("GB-NIR")) {
        flag = "GB_NIR"
    } else if(flag.equals("GB-SCT")) {
        flag = "GB_SCT"
    } else if(flag.equals("GB-WLS")) {
        flag = "GB_WLS"
    }

    return Question(country, flag);
}