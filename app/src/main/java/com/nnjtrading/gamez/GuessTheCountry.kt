package com.nnjtrading.gamez

import AnswerOptionLayout
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nnjtrading.gamez.ui.theme.*
import kotlinx.coroutines.delay
import org.w3c.dom.Text
import java.util.Random

/* References for out sources referred to achieve functions of this activity */

// GeeksforGeeks. (2019). How to Create an Alert Dialog Box in Android? [online]
// Available at: https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/.

class GuessTheCountry : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GamezTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    GuessTheCountryLayout()
                }
            }
        }
    }

    // an alert to display when user clicks the back button to prevent accidental destroying of activity
    // reference: How to Create an Alert Dialog Box in Android? available at https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/
    override fun onBackPressed() {

        var alert = android.app.AlertDialog.Builder(this)
        alert.setTitle("Are you sure?")
        alert.setMessage("Are you sure you want to quit the game?")
        alert.setPositiveButton("No") { dialog, which ->
            return@setPositiveButton
        }

        alert.setNegativeButton("Yes") { dialog, which ->
            this.finish()
        }

        alert.create().show()
    }
}

// function to get all countries from JSON file
private fun getCountries(context: Context): List<String> {
    val countries = Countries(context)
    return countries.countriesList()
}

// main function defining the layout and functions of the primary game.
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GuessTheCountryLayout() {

    val context = LocalContext.current

    val sharedPref = context.getSharedPreferences("TimerPreference", Context.MODE_PRIVATE)

    val checked by rememberSaveable { mutableStateOf(sharedPref.getBoolean("timer", false)) }

    var question by rememberSaveable { mutableStateOf(generateRandomFlag(context))}

    var flag: String by rememberSaveable { mutableStateOf(question.getFlag().lowercase())}

    var flagResourceId by rememberSaveable { mutableStateOf(getCountryFlagResourceId(context, flag))}

    var correctAnswers by rememberSaveable { mutableStateOf(0) }

    var isLoading by rememberSaveable { mutableStateOf(true) }

    val countries = getCountries(context)

    var selected by rememberSaveable { mutableStateOf(-1) }

    var answered by rememberSaveable { mutableStateOf(false) }

    var isCorrect by rememberSaveable { mutableStateOf(false) }

    var correctAnswer by rememberSaveable { mutableStateOf("") }

    var timerContDown by rememberSaveable { mutableStateOf(10) }

    var runTimer by rememberSaveable { mutableStateOf(true) }

    // run if timer is enabled
    if(checked) {
        LaunchedEffect(runTimer) {
            for(i in timerContDown downTo 1) {
                if(!answered) {
                    delay(1000)
                    timerContDown--
                } else {
                    runTimer = false
                    break
                }
            }
        }
    }

    // auto submit when timer ends
    if(timerContDown == 0) {
        if(selected > -1) {
            if (countries.get(selected)
                    .equals(question.getCountry())
            ) {
                isCorrect = true
                correctAnswers++
            } else {
                correctAnswer = question.getCountry()!!;
                isCorrect = false
            }
        } else {
            correctAnswer = question.getCountry()!!;
            isCorrect = false
        }
        answered = true
        runTimer = false
    }

    // correct answer
    Log.d("answer", question.getCountry()!!)

    GamezTheme {

        // gradient color background
        val gradient = Brush.linearGradient(
            0.0f to PrimaryPurple,
            10.0f to SecondPurple,
            start = Offset(0f,0f),
            end = Offset(0f,1500f)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(gradient)
        ) {
            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(gradient))
            {

                if(checked) {
                    Text(
                        text = "Timer: $timerContDown",
                        fontSize = 15.sp,
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(
                            0xFF3ce9bb
                        ),
                        modifier = Modifier.padding(30.dp, 10.dp, 0.dp, 0.dp).fillMaxWidth(),
                        fontFamily = FontFamily.Monospace
                    )
                } else {
                    Text(
                        text = "Relaxed Mode",
                        fontSize = 15.sp,
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(
                            0xFF3ce9bb
                        ),
                        modifier = Modifier.padding(30.dp, 10.dp, 0.dp, 0.dp).fillMaxWidth(),
                        fontFamily = FontFamily.Monospace
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(30.dp, 20.dp)
                        .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
                        .background(Color.White)
                        .verticalScroll(rememberScrollState())
                        .height(IntrinsicSize.Max)
                ) {

                    // show a timer progress bar when timer is enabled
                    if (checked) {
                        LinearProgressIndicator(progress = timerContDown.toFloat() / 10, modifier = Modifier.fillMaxWidth(),
                            color = if(timerContDown > 3) {
                                Color(0xFF3ce9bb)
                            } else {
                                Color(0xFFFF0000)
                            }
                        )
                    }

                    Text(
                        text = "Guess The Country",
                        fontSize = 20.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier
                            .padding(5.dp, 5.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(10.dp, 10.dp)
                            .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
                            .background(Color.Black)
                    ) {

                        // circular loading bar to show that the image is loading
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.width(54.dp),
                                color = SpecialGreen
                            )

                            Handler().postDelayed({
                                isLoading = false
                            }, 1000)

                        } else {

                            Box(modifier = Modifier.fillMaxSize()) {

                                Image(
                                    painter = painterResource(id = flagResourceId),
                                    contentDescription = "nameImage",
                                    modifier = Modifier
                                        .width(250.dp)
                                        .height(250.dp)
                                        .align(Alignment.Center)
                                        .padding(5.dp, 5.dp, 5.dp, 5.dp)
                                )

                                androidx.compose.animation.AnimatedVisibility(
                                    visible = answered,
                                    enter = fadeIn(initialAlpha = 0.4f),
                                    exit = fadeOut(animationSpec = tween(durationMillis = 250))
                                ) {

                                    // Element to show after submit button is clicked
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color(0xDD000000))
                                            .padding(10.dp, 0.dp)
                                    ) {
                                        if (isCorrect) {
                                            Text(
                                                text = "CORRECT",
                                                fontSize = 30.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xff00ff00)
                                            )
                                        } else {
                                            Text(
                                                text = "WRONG",
                                                fontSize = 30.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xffff0000)
                                            )
                                            Text(
                                                text = "Correct Answer:",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = correctAnswer ?: "",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xff3A90DC),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }

                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {

                        // initializing data class for each country options
                        var items by rememberSaveable {
                            mutableStateOf(
                                countries.map {
                                    AnswerOptionLayout(isSelected = false, title = "$it")
                                }
                            )
                        }

                        val scrollState = rememberLazyListState()

                        val screenHeight = LocalConfiguration.current.screenHeightDp.dp
                        val lazyColumnHeight: Dp = (screenHeight * 0.52f)

                        // Lazy column to show a scrollable set of options to choose as answer
                        LazyColumn(modifier = Modifier
                            .fillMaxWidth()
                            .height(lazyColumnHeight)
                            .padding(20.dp, 10.dp)
                            .align(
                                Alignment.TopCenter
                            )
                            .graphicsLayer { alpha = 0.99F }
                            .drawWithContent {
                                val colors = listOf(
                                    Color.Black,
                                    Color.Black,
                                    Color.Black,
                                    Color.Transparent
                                )
                                drawContent()
                                drawRect(
                                    brush = Brush.verticalGradient(colors),
                                    blendMode = BlendMode.DstIn
                                )
                            }, state = scrollState) {

                            items(items.size) { i ->
                                Column(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 3.dp, top = 3.dp, end = 3.dp, bottom =
                                        if (i == items.size - 1) {
                                            100.dp
                                        } else {
                                            3.dp
                                        }
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = Color.Gray.copy(0.8f),
                                        shape = RoundedCornerShape(size = 10.dp)
                                    )
                                    .clickable {

                                        // only selectable when not answered
                                        if (!answered) {
                                            items = items.mapIndexed { j, item ->
                                                if (i == j) {

                                                    item.copy(isSelected = true)

                                                } else {
                                                    item.copy(isSelected = false)
                                                }
                                            }

                                            selected = i
                                        }
                                    }
                                    .clip(RoundedCornerShape(size = 10.dp))
                                    .background(
                                        if (items.get(i).isSelected) SpecialGreen
                                        else Color.White
                                    )) {
                                    Box(
                                        modifier = Modifier
                                            .padding(start = 12.dp)
                                            .fillMaxWidth(),
                                    ) {
                                        Text(
                                            text = items.get(i).title,
                                            color = if (items.get(i).isSelected) Color.Black
                                            else Color.Gray.copy(0.8f),
                                            fontSize = MaterialTheme.typography.h6.fontSize,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier
                                                .padding(10.dp)
                                                .align(
                                                    Alignment.CenterStart
                                                )
                                        )

                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            modifier = Modifier
                                                .padding(10.dp)
                                                .align(
                                                    Alignment.CenterEnd
                                                ),
                                            contentDescription = "List Item Icon",
                                            tint =
                                            if (items.get(i).isSelected) Color.Black
                                            else Color.Gray.copy(0.8f)
                                        )
                                    }
                                }
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier
                                .fillMaxHeight()
                                .align(
                                    Alignment.BottomCenter
                                )
                        ) {
                            Button(
                                onClick = {
                                    // function to run when button name is next
                                    if (answered) {
                                        if (selected != -1 || timerContDown == 0) {

                                            isLoading = true
                                            answered = false
                                            isCorrect = false
                                            timerContDown = 10
                                            runTimer = true
                                            items = items.mapIndexed { j, item ->
                                                if (selected == j) {

                                                    item.copy(isSelected = false)

                                                } else {
                                                    item.copy(isSelected = false)
                                                }
                                            }
                                            selected = -1
                                            question = generateRandomFlag(context)
                                            flag = question.getFlag().toLowerCase();
                                            flagResourceId =
                                                getCountryFlagResourceId(context, flag)

                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Select a country to submit answer",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } 
                                    // function to run when button name is submit
                                    else {

                                        if (selected != -1) {
                                            if (countries.get(selected)
                                                    .equals(question.getCountry())
                                            ) {
                                                isCorrect = true
                                                correctAnswers++
                                                answered = true
                                            } else {
                                                correctAnswer = question.getCountry()!!;
                                                isCorrect = false
                                                answered = true
                                            }

                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Select a country to submit answer",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                }, modifier = Modifier
                                    .padding(30.dp, 50.dp, 30.dp, 20.dp)
                                    .fillMaxWidth()
                                    .height(40.dp), colors = ButtonDefaults.buttonColors(
                                    backgroundColor = buttonBackground
                                )
                            ) {

                                if (answered) {
                                    Text(text = "Next", color = Color.White)
                                } else {
                                    Text(text = "Submit", color = Color.White)
                                }

                            }
                        }
                    }

                }

            }
        }

    }
}

