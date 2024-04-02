
// link to demonstration video:

package com.nnjtrading.gamez

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradient
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nnjtrading.gamez.ui.theme.GamezTheme
import com.nnjtrading.gamez.ui.theme.PrimaryPurple
import com.nnjtrading.gamez.ui.theme.SecondPurple
import com.nnjtrading.gamez.openActivity as openActivity1

/* References for out sources referred to achieve functions of this activity */

// Digital Ocean (2022). Android SharedPreferences using Kotlin | DigitalOcean. [online]
// Available at: https://www.digitalocean.com/community/tutorials/android-sharedpreferences-kotlin.

// Stack Overflow (2021). Orientation on Jetpack Compose. [online]
// Available at: https://stackoverflow.com/questions/64753944/orientation-on-jetpack-compose [Accessed 2 Apr. 2024].

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GamezTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    // composable layout function containing all components to view on the screen
                    MainLayout()
                }
            }

        }
    }

}
// function to open game activity screen
private fun openActivity(type: String, context: Context) {

    if(type.equals("Guess The Country")) {
        val intent = Intent(context, GuessTheCountry::class.java)
        context.startActivity(intent)
    } else if(type.equals("Guess Hints")) {
        val intent = Intent(context, GuessHints::class.java)
        context.startActivity(intent)
    } else if(type.equals("Guess The Flag")) {
        val intent = Intent(context, GuessTheFlag::class.java)
        context.startActivity(intent)
    } else {
        val intent = Intent(context, Advanced::class.java)
        context.startActivity(intent)
    }
}

//@Preview(showBackground = true, showSystemUi = true,
//    device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainLayout() {

    val context = LocalContext.current

    // to get the orientation configuration of the application
    // reference: Orientation on Jetpack Compose available at https://stackoverflow.com/questions/64753944/orientation-on-jetpack-compose
    val configuration = LocalConfiguration.current
    var isLandscapeOrientation by rememberSaveable { mutableStateOf(false) }
    when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            isLandscapeOrientation = false
        }
        else -> {
            isLandscapeOrientation = true
        }
    }

    // shared preference to store and retrieve timer configuration
    // reference : Android SharedPreferences using Kotlin available at https://www.digitalocean.com/community/tutorials/android-sharedpreferences-kotlin
    val sharedPref = context.getSharedPreferences("TimerPreference", Context.MODE_PRIVATE)
    var TimerChecked by rememberSaveable { mutableStateOf(sharedPref.getBoolean("timer", false)) }

    GamezTheme {

        // background gradient color
        val gradient = Brush.linearGradient(
            0.0f to PrimaryPurple,
            10.0f to SecondPurple,
            start = Offset(0f, 0f),
            end = Offset(0f, 1500f)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(gradient)
        ) {
            // view to show when orientation is portrait
            if(!isLandscapeOrientation) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(0.dp, 20.dp)
                        .verticalScroll(rememberScrollState())
                        .height(IntrinsicSize.Max)
                ) {

                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                            .fillMaxWidth().align(Alignment.TopCenter)) {

                            // timer toggle switch
                            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(0.dp, 0.dp, 20.dp, 0.dp)) {
                                    Text(text = "Timer:   ", color = Color(0xffE7E7E7))
                                    Switch(
                                        checked = TimerChecked,
                                        onCheckedChange = {
                                            val sharedPreference =  context.getSharedPreferences("TimerPreference", Context.MODE_PRIVATE)
                                            with(sharedPreference.edit()) {
                                                putBoolean("timer", it)
                                                apply()
                                            }

                                            TimerChecked = it
                                        },

                                        )
                                }
                            }

                            // game app icon and name
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(painter =  painterResource(id = R.drawable.icon), contentDescription = "icon", modifier = Modifier
                                    .width(250.dp)
                                    .height(250.dp)
                                    .shadow(
                                        elevation = 20.dp,
                                        RoundedCornerShape(100.dp),
                                        false,
                                        Color.Black,
                                        Color.Black
                                    ))
                                Text(text = "G.A.M.E.Z", fontSize = 35.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.ExtraBold, color = Color(
                                    0xFF3ce9bb
                                ), modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 20.dp), fontFamily = FontFamily.Monospace)

                            }
                        }

                        // App Intro text and buttons to open game screens
                        Column(verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.align(
                                Alignment.BottomCenter)) {

                            Text(text = "Let's Play!", fontSize = 25.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.ExtraBold, color = Color.White, modifier = Modifier.padding(0.dp, 30.dp, 0.dp, 0.dp))
                            Text(text = "Are you ready to take up this challenge ??", color = Color(0xffE7E7E7), modifier = Modifier.padding(0.dp, 5.dp))
                            Text(text = "Choose a game to continue", color = Color(0xffE7E7E7), modifier = Modifier.padding(0.dp, 3.dp, 0.dp, 30.dp))

                            Button(onClick = { openActivity1(type = "Guess The Country", context) }, modifier = Modifier
                                .padding(30.dp, 10.dp)
                                .fillMaxWidth()
                                .height(40.dp), colors = ButtonDefaults.buttonColors(backgroundColor = Color(
                                0xFF6949FD
                            ))) {
                                Text(text = "\uD83C\uDF8C  Guess the Country  \uD83C\uDF8C", color = Color.White)

                            }

                            Button(onClick = { openActivity1(type = "Guess Hints", context) }, modifier = Modifier
                                .padding(30.dp, 10.dp)
                                .fillMaxWidth()
                                .height(40.dp), colors = ButtonDefaults.buttonColors(backgroundColor = Color(
                                0xFF6949FD
                            ))) {
                                Text(text = "\uD83C\uDF8C  Guess Hints  \uD83C\uDF8C " , color = Color.White)

                            }

                            Button(onClick = { openActivity1(type = "Guess The Flag", context) }, modifier = Modifier
                                .padding(30.dp, 10.dp)
                                .fillMaxWidth()
                                .height(40.dp), colors = ButtonDefaults.buttonColors(backgroundColor = Color(
                                0xFF6949FD
                            ))) {
                                Text(text = "\uD83C\uDF8C  Guess the Flag  \uD83C\uDF8C " , color = Color.White)

                            }

                            Button(onClick = { openActivity1(type = "Advanced", context) }, modifier = Modifier
                                .padding(30.dp, 10.dp)
                                .fillMaxWidth()
                                .height(40.dp), colors = ButtonDefaults.buttonColors(backgroundColor = Color(
                                0xFF6949FD
                            ))) {
                                Text(text = "\uD83C\uDF8C  Advanced  \uD83C\uDF8C " , color = Color.White)

                            }
                        }


                    }
                }
            }
            // view to display when orientation is landscape
            else {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(0.dp, 20.dp)
                ) {

                    Box(modifier = Modifier.fillMaxSize()) {

                        // landscape view
                        Row(modifier = Modifier.fillMaxSize()) {

                            // App logo and name
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f).fillMaxHeight()) {
                                Image(painter =  painterResource(id = R.drawable.icon), contentDescription = "icon", modifier = Modifier
                                    .width(220.dp)
                                    .height(220.dp)
                                    .shadow(
                                        elevation = 20.dp,
                                        RoundedCornerShape(100.dp),
                                        false,
                                        Color.Black,
                                        Color.Black
                                    ))
                                Text(text = "G.A.M.E.Z", fontSize = 35.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.ExtraBold, color = Color(
                                    0xFF3ce9bb
                                ), modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 20.dp), fontFamily = FontFamily.Monospace)

                            }

                            // Buttons to start games and timer trigger
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f))
                            {
                                Column( horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(0.dp, 0.dp, 20.dp, 0.dp)) {
                                        Text(text = "Timer:   ", color = Color(0xffE7E7E7))
                                        Switch(
                                            checked = TimerChecked,
                                            onCheckedChange = {
                                                val sharedPreference =  context.getSharedPreferences("TimerPreference", Context.MODE_PRIVATE)
                                                with(sharedPreference.edit()) {
                                                    putBoolean("timer", it)
                                                    apply()
                                                }

                                                TimerChecked = it
                                            },

                                            )
                                    }
                                }

                                Column(verticalArrangement = Arrangement.Bottom,
                                    modifier = Modifier
                                        .fillMaxHeight(),
                                    horizontalAlignment = Alignment.CenterHorizontally)
                                {

                                    Text(text = "Let's Play!", fontSize = 25.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.ExtraBold, color = Color.White, modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp))
                                    Text(text = "Are you ready to take up this challenge ??", color = Color(0xffE7E7E7), modifier = Modifier.padding(0.dp, 5.dp))
                                    Text(text = "Choose a game to continue", color = Color(0xffE7E7E7), modifier = Modifier.padding(0.dp, 3.dp, 0.dp, 3.dp))

                                    Button(onClick = { openActivity1(type = "Guess The Country", context) }, modifier = Modifier
                                        .padding(30.dp, 5.dp)
                                        .fillMaxWidth()
                                        .height(40.dp), colors = ButtonDefaults.buttonColors(backgroundColor = Color(
                                        0xFF6949FD
                                    ))) {
                                        Text(text = "\uD83C\uDF8C  Guess the Country  \uD83C\uDF8C", color = Color.White)

                                    }

                                    Button(onClick = { openActivity1(type = "Guess Hints", context) }, modifier = Modifier
                                        .padding(30.dp, 10.dp)
                                        .fillMaxWidth()
                                        .height(40.dp), colors = ButtonDefaults.buttonColors(backgroundColor = Color(
                                        0xFF6949FD
                                    ))) {
                                        Text(text = "\uD83C\uDF8C  Guess Hints  \uD83C\uDF8C " , color = Color.White)

                                    }

                                    Button(onClick = { openActivity1(type = "Guess The Flag", context) }, modifier = Modifier
                                        .padding(30.dp, 10.dp)
                                        .fillMaxWidth()
                                        .height(40.dp), colors = ButtonDefaults.buttonColors(backgroundColor = Color(
                                        0xFF6949FD
                                    ))) {
                                        Text(text = "\uD83C\uDF8C  Guess the Flag  \uD83C\uDF8C " , color = Color.White)

                                    }

                                    Button(onClick = { openActivity1(type = "Advanced", context) }, modifier = Modifier
                                        .padding(30.dp, 10.dp)
                                        .fillMaxWidth()
                                        .height(40.dp), colors = ButtonDefaults.buttonColors(backgroundColor = Color(
                                        0xFF6949FD
                                    ))) {
                                        Text(text = "\uD83C\uDF8C  Advanced  \uD83C\uDF8C " , color = Color.White)

                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}