package com.nnjtrading.gamez

import android.content.Context
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nnjtrading.gamez.ui.theme.*
import kotlinx.coroutines.delay

/* References for out sources referred to achieve functions of this activity */

// GeeksforGeeks. (2019). How to Create an Alert Dialog Box in Android? [online]
// Available at: https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/.

class Advanced : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GamezTheme () {
                    AdvancedLayout()
                }
            }
        }

    // an alert to display when user clicks the back button to prevent accidental destroying of activity
    // reference: How to Create an Alert Dialog Box in Android? available at https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/
    override fun onBackPressed() {
            val alert = android.app.AlertDialog.Builder(this)
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

// main function defining the layout and functions of the last game.
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdvancedLayout() {

    val context = LocalContext.current

    val sharedPref = context.getSharedPreferences("TimerPreference", Context.MODE_PRIVATE)

    val checked by rememberSaveable { mutableStateOf(sharedPref.getBoolean("timer", false)) }

    val questions by rememberSaveable { mutableStateOf(mutableListOf<Question>())}

    val flagResourceId by  rememberSaveable { mutableStateOf(mutableListOf<Int>()) }

    LaunchedEffect(Unit) {
        var count = 0

        while(count < 3) {
            val question = generateRandomFlag(context)
            if(!questions.contains(question)) {
                questions.add(question)
                flagResourceId.add(getCountryFlagResourceId(context, question.getFlag().toLowerCase()))
                count++
            }
        }
    }

    var correctAnswers by rememberSaveable { mutableStateOf(0) }

    var isLoading by rememberSaveable { mutableStateOf(true) }

    var wrongAttempts by rememberSaveable { mutableStateOf(0) }

    var answered by rememberSaveable { mutableStateOf(false) }

    val isCorrect: List<MutableState<Boolean>> = rememberSaveable {
        mutableListOf(
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false)
        )
    }

    var firstAnswered by rememberSaveable { mutableStateOf(false)};

    var correctAnswer by rememberSaveable { mutableStateOf(mutableListOf<String>("", "", "")) };

    var answerState: List<MutableState<String>> = rememberSaveable {
        mutableListOf(
            mutableStateOf(""),
            mutableStateOf(""),
            mutableStateOf("")
        )
    }

    var score by rememberSaveable { mutableStateOf(0) }

    var timerContDown by rememberSaveable { mutableStateOf(10) }

    var runTimer by rememberSaveable { mutableStateOf(true) }

    // correct answer
    LaunchedEffect(questions) {
        for(i in 0..2) {
            val count = i + 1
            println(count.toString() + ": " + questions.get(i).getCountry()!!)
            Log.d("answer for " + count.toString(), questions.get(i).getCountry()!!)
        }
    }

    // run if timer is enabled
    if(checked) {
        LaunchedEffect(runTimer) {
            println("running")
            for(i in timerContDown downTo 1) {
                if(!answered) {
                    delay(1000)
                    timerContDown--
                    if(timerContDown == 0) {
                        runTimer = false
                    }
                } else {
                    runTimer = false
                    break
                }
            }
        }
    }

    // auto submit when timer ends
    if(timerContDown == 0) {

        firstAnswered = true

        if (wrongAttempts < 3) {

            // check all three submitted answers
            for(i in 0..2) {
                if(answerState.any {it.value.uppercase() == questions.get(i).getCountry()!!.uppercase()}) {
                    if(!isCorrect[i].value) {
                        isCorrect[i].value = true
                        score++
                    }
                } else {
                    correctAnswer.set(i, questions.get(i).getCountry()!!)
                    answerState[i].value = ""
                }
            }

            // checking if there are any incorrect answers submitted
            if(isCorrect.any {!it.value}) {
                wrongAttempts++
                if(wrongAttempts == 3) {
                    answered = true
                } else {
                    timerContDown = 10
                    runTimer = true
                }
            }
            // return as correct answer when all three answers are correct
            else {
                answered = true
            }


        } else {
            answered = true
        }

    }

    GamezTheme {

        // gradient background fill
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

                Row(modifier = Modifier.fillMaxWidth()) {
                    if(checked) {
                        Text(
                            text = "Timer: $timerContDown",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(
                                0xFF3ce9bb
                            ),
                            modifier = Modifier
                                .padding(30.dp, 10.dp, 0.dp, 0.dp)
                                .weight(1f),
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
                            modifier = Modifier
                                .padding(30.dp, 10.dp, 0.dp, 0.dp)
                                .weight(1f),
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = "Score: $score",
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(
                            0xFF3ce9bb
                        ),
                        modifier = Modifier.padding(0.dp, 10.dp, 30.dp, 0.dp),
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

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp, 5.dp), horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "ADVANCED",
                            fontSize = 20.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier
                                .padding(5.dp, 5.dp)
                                .weight(1f),
                        )

                        Text(
                            text = "Wrong attempts: $wrongAttempts",
                            fontSize = 15.sp,
                            color = Color.Red,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier
                                .padding(5.dp, 8.dp),
                            textAlign = TextAlign.End
                        )
                    }

                    // first row with two flags
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)) {

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .padding(10.dp, 10.dp)
                                        .height(150.dp)
                                        .weight(1f)
                                        .clip(
                                            shape = RoundedCornerShape(
                                                15.dp,
                                                15.dp,
                                                15.dp,
                                                15.dp
                                            )
                                        )
                                        .background(Color(0xffE7E7E7))
                                ) {

                                    // first flag
                                    Box(modifier = Modifier.fillMaxSize()) {

                                        Column(verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .width(40.dp)
                                                .height(40.dp)
                                                .padding(8.dp)
                                                .clip(RoundedCornerShape(5.dp))
                                                .background(Color(0xffC7C7C7))) {


                                            Text(text = "01", fontWeight = FontWeight.Bold)
                                        }

                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier
                                                .fillMaxSize()
                                        ) {

                                            // circular progress bar to indicate first image is loading
                                            if (isLoading) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.width(54.dp),
                                                    color = SpecialGreen
                                                )

                                                Handler().postDelayed({
                                                    isLoading = false
                                                }, 1000)

                                            }
                                            else {

                                                Box(modifier = Modifier.fillMaxSize()) {

                                                    Image(
                                                        painter = painterResource(id = flagResourceId.get(0)),
                                                        contentDescription = "nameImage",
                                                        modifier = Modifier
                                                            .width(100.dp)
                                                            .height(100.dp)
                                                            .align(Alignment.Center)
                                                            .padding(5.dp, 5.dp, 5.dp, 5.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .padding(10.dp, 10.dp)
                                        .height(150.dp)
                                        .weight(1f)
                                        .clip(
                                            shape = RoundedCornerShape(
                                                15.dp,
                                                15.dp,
                                                15.dp,
                                                15.dp
                                            )
                                        )
                                        .background(Color(0xffE7E7E7))
                                ) {

                                    // second flag
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        Column(verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .width(40.dp)
                                                .height(40.dp)
                                                .padding(8.dp)
                                                .clip(RoundedCornerShape(5.dp))
                                                .background(Color(0xffC7C7C7))) {


                                            Text(text = "02", fontWeight = FontWeight.Bold)
                                        }

                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier
                                                .fillMaxSize()
                                        ) {
                                            if (isLoading) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.width(54.dp),
                                                    color = SpecialGreen
                                                )

                                                Handler().postDelayed({
                                                    isLoading = false
                                                }, 1000)

                                            }
                                            else {

                                                Box(modifier = Modifier.fillMaxSize()) {

                                                    Image(
                                                        painter = painterResource(id = flagResourceId.get(1)),
                                                        contentDescription = "nameImage",
                                                        modifier = Modifier
                                                            .width(100.dp)
                                                            .height(100.dp)
                                                            .align(Alignment.Center)
                                                            .padding(5.dp, 5.dp, 5.dp, 5.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                }
                            }

                            // display third flag in new line
                            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp, 0.dp)
                                .height(150.dp)) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .height(150.dp)
                                        .padding(10.dp, 10.dp)
                                        .layout { measurable, constraints ->
                                            val maxWidthAllowed = constraints.maxWidth
                                            val placeable = measurable.measure(
                                                constraints.copy(minWidth = maxWidthAllowed / 2)
                                            )

                                            layout(placeable.width, placeable.height) {
                                                placeable.placeRelative(
                                                    0,
                                                    0
                                                )
                                            }
                                        }
                                        .clip(
                                            shape = RoundedCornerShape(
                                                15.dp,
                                                15.dp,
                                                15.dp,
                                                15.dp
                                            )
                                        )
                                        .background(Color(0xffE7E7E7))
                                ) {

                                    // third flag
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        Column(verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .width(40.dp)
                                                .height(40.dp)
                                                .padding(8.dp)
                                                .clip(RoundedCornerShape(5.dp))
                                                .background(Color(0xffC7C7C7))) {


                                            Text(text = "03", fontWeight = FontWeight.Bold)
                                        }

                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier
                                                .fillMaxSize()
                                        ) {
                                            if (isLoading) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.width(54.dp),
                                                    color = SpecialGreen
                                                )

                                                Handler().postDelayed({
                                                    isLoading = false
                                                }, 1000)

                                            }
                                            else {

                                                Box(modifier = Modifier.fillMaxSize()) {

                                                    Image(
                                                        painter = painterResource(id = flagResourceId.get(2)),
                                                        contentDescription = "nameImage",
                                                        modifier = Modifier
                                                            .width(100.dp)
                                                            .height(100.dp)
                                                            .align(Alignment.Center)
                                                            .padding(5.dp, 5.dp, 5.dp, 5.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                        }

                        // animated fade in and out of results after submision
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
                                    .background(Color(0xDDffffff))
                                    .padding(10.dp, 0.dp)
                            ) {
                                // show result according to guesses
                                if (isCorrect.any {!it.value}) {
                                    Text(
                                        text = "WRONG",
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xffff0000)
                                    )

                                    Text(
                                        text = "Correct Answers are displayed below the corresponding text boxes",
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Normal,
                                        color = Color(0xff000000)
                                    )
                                } else {
                                    Text(
                                        text = "CORRECT",
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xff00ff00)
                                    )
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {


                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp, 0.dp, 10.dp, bottom =
                                if (isCorrect.any { !it.value } && answered) {
                                    60.dp
                                } else {
                                    70.dp
                                }
                                )
                                .align(
                                    Alignment.TopCenter
                                )
                        ) {

                            // loop to create three text boxes corresponding to the relevant flags
                            for(i in 0..2) {

                                val count = i + 1

                                Column(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        top = 0.dp, start = 0.dp, end = 0.dp, bottom =
                                        if (!isCorrect[i].value && answered) {
                                            5.dp
                                        } else {
                                            10.dp
                                        }
                                    )) {
                                    Row(verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .padding(top = 10.dp, start = 0.dp, end = 10.dp, bottom =
                                            if(!isCorrect[i].value && answered) {
                                                0.dp
                                            } else {
                                                10.dp
                                            }
                                            )
                                    ) {
                                        Column(verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .width(40.dp)
                                                .height(40.dp)
                                                .padding(8.dp)
                                                .clip(RoundedCornerShape(5.dp))
                                                .background(
                                                    if (isCorrect.get(i).value) {
                                                        Color(0x2000FF00)
                                                    } else if (firstAnswered) {
                                                        Color(0x20FF0000)
                                                    } else {
                                                        Color(0xffE7E7E7)
                                                    }
                                                )) {


                                            Text(text = "0$count", fontWeight = FontWeight.Bold, color =
                                            if (isCorrect.get(i).value) {
                                                Color(0x60008800)
                                            } else if(firstAnswered) {
                                                Color(0xff880000)
                                            } else {
                                                Color(0xff000000)
                                            }
                                            )
                                        }

                                        // displaying versions of text boxes according to state
                                        TextField(
                                            value = answerState[i].value,
                                            placeholder = { Text(text = "Enter country name") },
                                            onValueChange = {
                                                if (it.length < 60) {
                                                    answerState[i].value = it
                                                }
                                            },
                                            enabled =
                                            if(isCorrect[i].value) {
                                                false
                                            } else !answered,

                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(
                                                    width = if (isCorrect.get(i).value || firstAnswered) {
                                                        1.3.dp
                                                    } else {
                                                        0.dp
                                                    },
                                                    color = if (isCorrect.get(i).value) {
                                                        Color(0x60008800)
                                                    } else if (firstAnswered) {
                                                        Color(0xff880000)
                                                    } else {
                                                        Color.Transparent
                                                    },
                                                    shape = RoundedCornerShape(8.dp)
                                                ),
                                            shape = RoundedCornerShape(8.dp),
                                            colors =
                                            // show green background when answer is correct
                                            if(isCorrect.get(i).value) {
                                                TextFieldDefaults.textFieldColors(
                                                    backgroundColor = Color(0x2000FF00),
                                                    focusedIndicatorColor = Color.Transparent,
                                                    placeholderColor = Color.Gray,
                                                    unfocusedIndicatorColor = Color.Transparent,
                                                    cursorColor = Color.Black,
                                                    textColor = Color(0x60008800),
                                                    disabledIndicatorColor = Color.Transparent
                                                )
                                            }
                                            // show red background when answer is wrong
                                            else if(firstAnswered) {
                                                TextFieldDefaults.textFieldColors(
                                                    backgroundColor = Color(0x20FF0000),
                                                    focusedIndicatorColor = Color.Transparent,
                                                    placeholderColor = Color.Gray,
                                                    unfocusedIndicatorColor = Color.Transparent,
                                                    cursorColor = Color.Black,
                                                    textColor = Color(0xff880000),
                                                    disabledIndicatorColor = Color.Transparent
                                                )
                                            }
                                            // show normal light gray background when no answer is submitted
                                            else {
                                                TextFieldDefaults.textFieldColors(
                                                    backgroundColor = Color(0xffE7E7E7),
                                                    focusedIndicatorColor = Color.Transparent,
                                                    placeholderColor = Color.Gray,
                                                    unfocusedIndicatorColor = Color.Transparent,
                                                    cursorColor = Color.Black,
                                                    textColor = Color.Black
                                                )
                                            }
                                        )
                                    }

                                    // show correct answer under corresponding text boxes only when submited answers are wrong
                                    if(!isCorrect[i].value && answered) {
                                        var answer = correctAnswer[i]

                                        Text(
                                            text = "$answer",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xff3A90DC),
                                            modifier = Modifier.padding(46.dp, 2.dp, 0.dp, 0.dp)
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

                                    // run when button is labeled next
                                    if (answered) {

                                        isLoading = true
                                        answered = false
                                        firstAnswered = false
                                        runTimer = true
                                        timerContDown = 10
                                        wrongAttempts = 0
                                        questions.clear()
                                        flagResourceId.clear()
                                        var count = 0
                                        while(count < 3) {
                                            val question = generateRandomFlag(context)
                                            if(!questions.contains(question)) {
                                                questions.add(question)

                                                flagResourceId.add(getCountryFlagResourceId(context, question.getFlag().toLowerCase()))
                                                answerState[count].value = ""
                                                isCorrect[count].value = false
                                                count++
                                            }
                                        }

                                    }
                                    // run when button is labled submit
                                    else {

                                        if (answerState.any { it.value == "" }) {
                                            Toast.makeText(
                                                context,
                                                "Enter all country names to Submit",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        } else {

                                            firstAnswered = true

                                            if (wrongAttempts < 3) {

                                                for(i in 0..2) {
                                                    if(answerState.any {it.value.uppercase() == questions.get(i).getCountry()!!.uppercase()}) {
                                                        if(!isCorrect[i].value) {
                                                            isCorrect[i].value = true
                                                            score++
                                                        }
                                                    } else {
                                                        correctAnswer.set(i, questions.get(i).getCountry()!!)
                                                        answerState[i].value = ""
                                                    }
                                                }

                                                if(isCorrect.any {!it.value}) {
                                                    wrongAttempts++
                                                    if(wrongAttempts == 3) {
                                                        answered = true
                                                    } else {
                                                        runTimer = false
                                                        timerContDown = 10
                                                        Handler().postDelayed({
                                                            runTimer = true
                                                        }, 1000)
                                                    }
                                                } else {
                                                    answered = true
                                                    correctAnswers++
                                                }


                                            } else {
                                                answered = true
                                            }

                                        }


                                    }


                                }, modifier = Modifier
                                    .padding(30.dp, 30.dp, 30.dp, 20.dp)
                                    .fillMaxWidth()
                                    .height(40.dp), colors = ButtonDefaults.buttonColors(
                                    backgroundColor = buttonBackground
                                )
                            ) {

                                if (answered) {
                                    Text(text = "Continue", color = Color.White)
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