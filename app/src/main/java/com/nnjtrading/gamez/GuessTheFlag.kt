package com.nnjtrading.gamez

import AnswerOptionLayout
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import java.util.*

/* References for out sources referred to achieve functions of this activity */

// GeeksforGeeks. (2019). How to Create an Alert Dialog Box in Android? [online]
// Available at: https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/.

class GuessTheFlag : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GamezTheme {

                Surface() {
                    GuessTheFlagLayout()
                }
            }
        }
    }
    // an alert to display when user clicks the back button to prevent accidental destroying of activity
    // reference: How to Create an Alert Dialog Box in Android? available at https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/
    override fun onBackPressed() {
        val alert = AlertDialog.Builder(this)
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

// function to generate two other answer option flags
private fun generateRandomFlagOptions(context: Context, question: String): MutableList<String> {
    val countries = Countries(context)
    val countryCodesMap = countries.countriesCodeList()

    val generatedCodes = mutableListOf<String>()

    val RandomNumberGenerator = Random()

    var count  = 0;

    while(count < 2) {
        val randomNumber = RandomNumberGenerator.nextInt(255);
        var flag = countryCodesMap.get(randomNumber)

        // replacing middle slashes with underscore to resolve android naming error in drawable
        if(!flag.equals(question)) {
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

            if(!generatedCodes.contains(flag.lowercase())){
                generatedCodes.add(flag.lowercase())
                count++
            }
        }
    }

    return generatedCodes
}

// main function defining the layout and functions of the third game.
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GuessTheFlagLayout() {

    val context = LocalContext.current

    val sharedPref = context.getSharedPreferences("TimerPreference", Context.MODE_PRIVATE)

    var checked by rememberSaveable { mutableStateOf(sharedPref.getBoolean("timer", false)) }

    var question by rememberSaveable { mutableStateOf(generateRandomFlag(context)) }

    var flag by rememberSaveable { mutableStateOf(question.getFlag().lowercase()) }

    var generatedRandomFlags by rememberSaveable { mutableStateOf(generateRandomFlagOptions(context, flag))}

    var RandomNumberGenerator = Random()

    var randomNumber by rememberSaveable { mutableStateOf(RandomNumberGenerator.nextInt(3))}

    generatedRandomFlags.add(randomNumber, flag)

    var flagResourceId by rememberSaveable { mutableStateOf(mutableListOf<Int>())}

    // for loop to get all three flag resourse IDs
    for(flagIndex in generatedRandomFlags) {
        flagResourceId.add(getCountryFlagResourceId(context, flagIndex))
    }

    var correctAnswers by rememberSaveable { mutableStateOf(0) }

    var selected by rememberSaveable { mutableStateOf(-1) }

    var isLoading by rememberSaveable { mutableStateOf(true) }

    var answered by rememberSaveable { mutableStateOf(false) }

    var isCorrect by rememberSaveable { mutableStateOf(false) }

    var correctAnswer by rememberSaveable { mutableStateOf(randomNumber + 1) }

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
        if ((selected + 1) == correctAnswer) {
            isCorrect = true
            correctAnswers++
        } else {
            isCorrect = false
        }
        answered = true
        runTimer = false
    }

    // correct answer
    Log.d("answer", (randomNumber + 1).toString())

    GamezTheme {

        // background gradient fill
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
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(gradient)
            )
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

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(30.dp, 20.dp)
                        .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
                        .background(Color.White)
                        .verticalScroll(rememberScrollState())
                        .height(IntrinsicSize.Max)
                ) {

                    // show timer progress if timer option enabled
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
                        text = "Guess The Flag",
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
                            .height(150.dp)
                            .padding(10.dp, 10.dp)
                            .clip(shape = RoundedCornerShape(10.dp, 10.dp, 10.dp, 10.dp))
                            .background(Color.Black)
                    ) {

                        // circular loading bar to show that the country name is loading
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

                                var questionCountry = question.getCountry();

                                Text(text = "$questionCountry", modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Center)
                                    .padding(5.dp, 5.dp, 5.dp, 5.dp),
                                    fontSize = 26.sp, textAlign = TextAlign.Center, color= Color.White
                                )

                                // fade in animation to show result after submitting answer
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = answered,
                                    enter = fadeIn(initialAlpha = 0.4f),
                                    exit = fadeOut(animationSpec = tween(durationMillis = 250))
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color(0xDD000000))
                                            .padding(10.dp, 0.dp)
                                    ) {

                                        // show result according to selected option
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
                                                text = "Option $correctAnswer",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Normal,
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

                        var items by rememberSaveable {
                            mutableStateOf(
                                flagResourceId .map {
                                    AnswerOptionLayout(isSelected = false, title = "$it")
                                }
                            )
                        }

                        val scrollState = rememberLazyListState()

                        // determining the height of the lazy column
                        val screenHeight = LocalConfiguration.current.screenHeightDp.dp
                        val lazyColumnHeight: Dp = (screenHeight * 0.62f)

                        // lazy colum to represent a scrollable set of answers to choose from
                        LazyColumn(modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp, 10.dp, 20.dp, 10.dp)
                            .height(lazyColumnHeight)
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
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 3.dp, top = 10.dp, end = 3.dp, bottom =
                                        if (i == items.size - 1) {
                                            100.dp
                                        } else {
                                            3.dp
                                        }
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = Color.Gray.copy(0.8f),
                                        shape = RoundedCornerShape(size = 5.dp)
                                    )
                                    .clip(RoundedCornerShape(size = 5.dp))
                                    .background(
                                        if (items.get(i).isSelected) SpecialGreen
                                        else Color.Gray.copy(0.08f)
                                    )) {

                                    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .weight(1f)) {
                                        Image(
                                            painter = painterResource(id = flagResourceId.get(i)),
                                            contentDescription = "nameImage",
                                            modifier = Modifier
                                                .width(150.dp)
                                                .height(150.dp)
                                                .clickable {

                                                    // making image clickable only when not answered
                                                    if (!answered) {
                                                        items = items.mapIndexed { j, item ->
                                                            if (i == j) {

                                                                item.copy(isSelected = true)

                                                            } else {
                                                                item.copy(isSelected = false)
                                                            }
                                                        }

                                                        selected = i;
                                                    }
                                                }
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        modifier = Modifier
                                            .padding(10.dp),
                                        contentDescription = "List Item Icon",
                                        tint =
                                        if (items.get(i).isSelected) Color.Black
                                        else Color.Gray.copy(0.8f)
                                    )
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

                                    // run when answer has been submitted
                                    if (answered) {
                                        if (selected != -1 || timerContDown == 0) {

                                            isLoading = true
                                            answered = false
                                            isCorrect = false
                                            runTimer = true
                                            timerContDown = 10
                                            items = items.mapIndexed { j, item ->
                                                if (selected == j) {

                                                    item.copy(isSelected = false)

                                                } else {
                                                    item.copy(isSelected = false)
                                                }
                                            }
                                            selected = -1
                                            question = generateRandomFlag(context)
                                            flag = question.getFlag().toLowerCase()
                                            generatedRandomFlags = generateRandomFlagOptions(context, flag)
                                            RandomNumberGenerator = Random()
                                            randomNumber = RandomNumberGenerator.nextInt(3)
                                            generatedRandomFlags.add(randomNumber, flag)
                                            flagResourceId = mutableListOf<Int>()
                                            for(flag in generatedRandomFlags) {
                                                flagResourceId.add(getCountryFlagResourceId(context, flag))
                                            }
                                            correctAnswer = randomNumber + 1

                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Select a Flag to submit answer",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    // run when answer needs to be submitted
                                    else {

                                        // checking is timer is 0 or user has selected any option
                                        if (selected != -1 || timerContDown == 0) {
                                            if ((selected + 1) == correctAnswer) {
                                                isCorrect = true
                                                correctAnswers++
                                                answered = true
                                            } else {
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

                                Text(text = "Next", color = Color.White)

                            }
                        }
                    }

                }

            }
        }

    }
}

