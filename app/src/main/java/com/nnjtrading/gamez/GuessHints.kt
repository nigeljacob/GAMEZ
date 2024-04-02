package com.nnjtrading.gamez

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

/* References for out sources referred to achieve functions of this activity */

// GeeksforGeeks. (2019). How to Create an Alert Dialog Box in Android? [online]
// Available at: https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/.

class GuessHints : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GuessHintsLayout()
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

// main function defining the layout and functions of the second game.
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GuessHintsLayout() {

    val context = LocalContext.current

    val configuration = LocalConfiguration.current

    val sharedPref = context.getSharedPreferences("TimerPreference", Context.MODE_PRIVATE)

    val checked by rememberSaveable { mutableStateOf(sharedPref.getBoolean("timer", false)) }

    var question by rememberSaveable {mutableStateOf(generateRandomFlag(context))}

    var flag by rememberSaveable { mutableStateOf(question.getFlag().lowercase()) }

    var flagResourceId by  rememberSaveable { mutableStateOf(getCountryFlagResourceId(context, flag)) }

    var correctAnswers by rememberSaveable { mutableStateOf(0) }

    var isLoading by rememberSaveable { mutableStateOf(true) }

    var wrongAttempts by rememberSaveable { mutableStateOf(0) }

    var answered by rememberSaveable { mutableStateOf(false) }

    var isCorrect by rememberSaveable { mutableStateOf(false) }

    var correctAnswer by rememberSaveable { mutableStateOf("") }

    var answerState by rememberSaveable { mutableStateOf("") }

    val enteredGuesses by rememberSaveable { mutableStateOf(mutableListOf<String>()) }

    var numberOfCharacters by rememberSaveable { mutableStateOf(0) }

    if(numberOfCharacters == 0) {
        for(Character in question.getCountry()!!) {
            if(!Character.toString().equals(" ") && !Character.toString().equals("(") && !Character.toString().equals(")")) numberOfCharacters++
        }
    }

    var timerContDown by rememberSaveable { mutableStateOf(10) }

    var runTimer by rememberSaveable { mutableStateOf(true) }

    // correct answer
    Log.d("answer", question.getCountry()!!)

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

        if (wrongAttempts < 3) {

            var available = false

            var numberOfAvailability = 0

            var letter = ""

            if (!enteredGuesses.contains(answerState)) {

                for (character in question.getCountry()!!) {
                    if (character.toString().toUpperCase()
                            .equals(answerState)
                    ) {
                        letter = character.toString()
                        numberOfAvailability++
                        available = true
                    }
                }

                // run when typed in letter is available
                if (available) {

                    for (i in 1..numberOfAvailability) {
                        enteredGuesses.add(letter.toUpperCase())
                    }

                    if (enteredGuesses.size == numberOfCharacters) {
                        correctAnswers++
                        isCorrect = true
                    }

                    timerContDown = 10
                    runTimer = true

                }
                // if letter not available show as wrong attempt
                else {
                    wrongAttempts++
                    if (wrongAttempts == 3) {
                        isCorrect = false
                        answered = true
                        correctAnswer =
                            question.getCountry()!!
                    } else {
                        timerContDown = 10
                        runTimer = true
                        Toast.makeText(
                            context,
                            "Times Up!!... You've got " + (3 - wrongAttempts) + " attempts more",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


            } else {
                Toast.makeText(
                    context,
                    "Already Inserted... Try a different letter",
                    Toast.LENGTH_SHORT
                ).show()

                // start timer again for the next attempt
                timerContDown = 10
                runTimer = true
                answered = false
            }


        } else {
            isCorrect = false
            answered = true
            correctAnswer = question.getCountry()!!
        }

        answerState = ""
    }

    GamezTheme {
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

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp, 5.dp), horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Guess Hints",
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
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color(0xDD000000))
                                            .padding(10.dp, 0.dp)
                                    ) {

                                        // show result according to guess
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
                                                text = "$correctAnswer",
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

                    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
                    var height: Dp = (screenHeight * 0.6f)

                    // determine height depending on orientation
                    when (configuration.orientation) {
                        Configuration.ORIENTATION_PORTRAIT -> {
                            height = (screenHeight * 0.5f)
                        }

                        else -> {
                            (screenHeight * 0.6f)
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
                                .height(height)
                                .padding(20.dp, 10.dp)
                                .align(
                                    Alignment.TopCenter
                                )
                        ) {

                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 20.dp)) {

                                // create blank space for every letter
                                for (character in question.getCountry()!!) {

                                    var characterUpperCase = character.toString().toUpperCase()

                                    if (!enteredGuesses.contains(characterUpperCase.toString())) {
                                        characterUpperCase = "  "
                                    }

                                    Text(
                                        text = "$characterUpperCase",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Black,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(5.dp, 0.dp)
                                            .drawBehind {

                                                // skip spaces and parenthesis found in word
                                                if (!character
                                                        .toString()
                                                        .equals(" ") && !character
                                                        .toString()
                                                        .equals("(") && !character
                                                        .toString()
                                                        .equals(")")
                                                ) {
                                                    if(answered) {
                                                        if (characterUpperCase == "  ") {

                                                            drawLine(
                                                                Color.Red,
                                                                Offset(0f, size.height),
                                                                Offset(size.width, size.height),
                                                                strokeWidth = 2.dp.toPx()
                                                            )
                                                        } else {
                                                            drawLine(
                                                                SpecialGreen,
                                                                Offset(0f, size.height),
                                                                Offset(size.width, size.height),
                                                                strokeWidth = 2.dp.toPx()
                                                            )
                                                        }

                                                    } else {
                                                        if (characterUpperCase == "  ") {

                                                            drawLine(
                                                                Color.LightGray,
                                                                Offset(0f, size.height),
                                                                Offset(size.width, size.height),
                                                                strokeWidth = 2.dp.toPx()
                                                            )
                                                        } else {
                                                            drawLine(
                                                                SpecialGreen,
                                                                Offset(0f, size.height),
                                                                Offset(size.width, size.height),
                                                                strokeWidth = 2.dp.toPx()
                                                            )
                                                        }
                                                    }
                                                }

                                            })
                                }
                            }

                            TextField(
                                value = answerState.uppercase(),
                                placeholder = { Text(text = "Enter a Letter here") },
                                onValueChange = {
                                    if (it.length < 2) answerState = it.uppercase()
                                },
                                enabled = !answered,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(0.dp, 30.dp, 0.dp, 50.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = TextFieldDefaults.textFieldColors(
                                    backgroundColor = Color(0xffE7E7E7),
                                    focusedIndicatorColor = Color.Transparent,
                                    placeholderColor = Color.Gray,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    cursorColor = Color.Black,
                                    textColor = Color.Black,
                                    disabledIndicatorColor = Color.Transparent
                                )
                            )

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

                                    // run when button name is next
                                    if (answered) {

                                        isLoading = true
                                        answered = false
                                        isCorrect = false
                                        runTimer = true
                                        timerContDown = 10
                                        wrongAttempts = 0
                                        numberOfCharacters = 0
                                        enteredGuesses.clear()
                                        question = generateRandomFlag(context)
                                        flag = question.getFlag().toLowerCase();
                                        flagResourceId =
                                            getCountryFlagResourceId(context, flag)
                                        for (Character in question.getCountry()!!) {
                                            if (Character.toString() != " " && Character.toString() != "(" && Character.toString() != ")") numberOfCharacters++
                                        }

                                    }

                                    // run when button is named submit
                                    else {

                                        // check is user is typed in a letter
                                        if (answerState.equals("")) {
                                            Toast.makeText(
                                                context,
                                                "Enter a letter to submit answer",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        } else {

                                            runTimer = false

                                            if (wrongAttempts < 3) {

                                                var available = false;

                                                var numberOfAvailability = 0

                                                var letter = ""

                                                if (!enteredGuesses.contains(answerState)) {

                                                    // check if letter is avaiable
                                                    for (character in question.getCountry()!!) {
                                                        if (character.toString().toUpperCase()
                                                                .equals(answerState)
                                                        ) {
                                                            letter = character.toString()
                                                            numberOfAvailability++
                                                            available = true
                                                        }
                                                    }

                                                    // add letter to list if available
                                                    if (available) {

                                                        for (i in 1..numberOfAvailability) {
                                                            enteredGuesses.add(letter.toUpperCase())
                                                        }

                                                        if (enteredGuesses.size == numberOfCharacters) {
                                                            correctAnswers++
                                                            isCorrect = true
                                                            answered = true
                                                        }

                                                        timerContDown = 10
                                                        Handler().postDelayed({
                                                            runTimer = true
                                                        }, 1000)

                                                    }

                                                    // add as wrong attempt when entered guess is not available
                                                    else {
                                                        wrongAttempts++
                                                        if (wrongAttempts == 3) {
                                                            answered = true
                                                            isCorrect = false
                                                            correctAnswer =
                                                                question.getCountry()!!
                                                            runTimer = false
                                                        } else {
                                                            timerContDown = 10
                                                            Handler().postDelayed({
                                                                runTimer = true
                                                            }, 1000)
                                                            Toast.makeText(
                                                                context,
                                                                "Not Available... You've got " + (3 - wrongAttempts) + " attempts more",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }


                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Already Inserted... Try a different letter",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    timerContDown = 10
                                                    Handler().postDelayed({
                                                        runTimer = true
                                                    }, 1000)
                                                }

                                            } else {
                                                answered = true
                                                isCorrect = false
                                                correctAnswer = question.getCountry()!!
                                            }

                                            answerState = ""

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