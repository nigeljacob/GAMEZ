package com.nnjtrading.gamez

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nnjtrading.gamez.ui.theme.*
import java.util.*

class GuessHints : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GuessHintsLayout()
        }
    }

    fun generateRandomFlag(context: Context): Question {

        val countries = Countries(this)

        val countryCodesMap = countries.countriesMap();

        val keysList = countries.countriesCodeList()

        var RandomNumberGenerator = Random()
        var randomNumber = RandomNumberGenerator.nextInt(255);

        var flag = keysList.get(randomNumber);

        var country = countryCodesMap.get(flag)

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

    fun getCountryFlagResourceId(context: Context, countryCode: String,): Int {
        return context.resources.getIdentifier(countryCode, "drawable", context.packageName)
    }
    
    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun GuessHintsLayout() {

        val context = this

        var question by rememberSaveable {mutableStateOf(generateRandomFlag(this))}

        var flag by rememberSaveable { mutableStateOf(question.getFlag().toLowerCase()) }

        var flagResourceId by  rememberSaveable { mutableStateOf(getCountryFlagResourceId(this, flag)) }

        var completedQuestions by  rememberSaveable { mutableStateOf(1) }

        var correctAnswers by rememberSaveable { mutableStateOf(0) }

        var didWin by rememberSaveable { mutableStateOf(correctAnswers > 15) }

        var isLoading by rememberSaveable { mutableStateOf(true) }

        var wrongAttempts by rememberSaveable { mutableStateOf(0) }

        var answered by rememberSaveable { mutableStateOf(false) }

        var isCorrect by rememberSaveable { mutableStateOf(false) };

        var correctAnswer by rememberSaveable { mutableStateOf("") };

        var answerState by rememberSaveable { mutableStateOf("") }

        var Name = getUserName(this)

        var enteredGuesses by rememberSaveable { mutableStateOf(mutableListOf<String>()) }

        var numberOfCharacters by rememberSaveable { mutableStateOf(0) }

        if(numberOfCharacters == 0) {
            for(Character in question.getCountry()!!) {
                if(!Character.toString().equals(" ") && !Character.toString().equals("(") && !Character.toString().equals(")")) numberOfCharacters++
            }
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

                    if (completedQuestions < 21) {
                        Text(
                            text = "$completedQuestions / 20",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.ExtraBold,
                            color = SpecialGreen,
                            modifier = Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp),
                            fontFamily = FontFamily.Monospace
                        )
                    } else {
                        Text(
                            text = "Completed",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(
                                0xFF3ce9bb
                            ),
                            modifier = Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp),
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                            .padding(30.dp, 30.dp)
                            .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
                            .background(Color.White)
                    ) {

                        if (completedQuestions < 21) {

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
                                                        color = Color.White,
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


                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp, 10.dp)
                                        .align(
                                            Alignment.TopCenter
                                        )
                                ) {

                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(0.dp, 20.dp)) {

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

                                                        if (!character
                                                                .toString()
                                                                .equals(" ") && !character
                                                                .toString()
                                                                .equals("(") && !character
                                                                .toString()
                                                                .equals(")")
                                                        ) {
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

                                                    })
                                        }
                                    }

                                    TextField(
                                        value = answerState.toUpperCase(),
                                        placeholder = { Text(text = "Enter a Letter here") },
                                        onValueChange = {
                                            if (it.length < 2) answerState = it.toUpperCase()
                                        },
                                        enabled = !answered,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(0.dp, 30.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = TextFieldDefaults.textFieldColors(
                                            backgroundColor = Color(0xffE7E7E7),
                                            focusedIndicatorColor = Color.Transparent,
                                            placeholderColor = Color.Gray,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            cursorColor = Color.Black,
                                            textColor = Color.Black
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

                                            if (answered) {

                                                completedQuestions++

                                                if (completedQuestions < 21) {
                                                    isLoading = true
                                                    answered = false
                                                    isCorrect = false
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

                                            } else {

                                                if (answerState.equals("")) {
                                                    Toast.makeText(
                                                        context,
                                                        "Enter a letter to submit answer",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                } else {

                                                    if (wrongAttempts < 3) {

                                                        var available = false;

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

                                                            if (available) {

                                                                for (i in 1..numberOfAvailability) {
                                                                    enteredGuesses.add(letter.toUpperCase())
                                                                }

                                                                if (enteredGuesses.size == numberOfCharacters) {
                                                                    correctAnswers++
                                                                    isCorrect = true
                                                                    answered = true
                                                                }

                                                            } else {
                                                                wrongAttempts++
                                                                if (wrongAttempts == 3) {
                                                                    answered = true
                                                                    isCorrect = false
                                                                    correctAnswer =
                                                                        question.getCountry()!!
                                                                } else {
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
                                            .padding(30.dp, 30.dp)
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

                        } else {

                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = "Your Results",
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier
                                        .padding(5.dp, 20.dp)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )

                                Column(
                                    verticalArrangement = Arrangement.Center, modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                ) {

                                    if (didWin) {

                                        Box(modifier = Modifier.fillMaxWidth()) {
                                            Image(
                                                painter = painterResource(id = R.drawable.cup),
                                                contentDescription = "nameImage",
                                                modifier = Modifier
                                                    .width(250.dp)
                                                    .height(250.dp)
                                                    .align(Alignment.Center)
                                                    .padding(5.dp, 10.dp, 5.dp, 20.dp)
                                            )
                                        }

                                        Text(
                                            text = "Congratulations!!",
                                            fontSize = 23.sp,
                                            color = Color.Black,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier
                                                .padding(5.dp, 10.dp)
                                                .fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )

                                    } else {

                                        if (correctAnswers > 7) {
                                            Box(modifier = Modifier.fillMaxWidth()) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.starts),
                                                    contentDescription = "nameImage",
                                                    modifier = Modifier
                                                        .width(250.dp)
                                                        .height(250.dp)
                                                        .align(Alignment.Center)
                                                        .padding(5.dp, 10.dp, 5.dp, 20.dp)
                                                )
                                            }

                                            Text(
                                                text = "Great Job!!",
                                                fontSize = 23.sp,
                                                color = Color.Black,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier = Modifier
                                                    .padding(5.dp, 10.dp)
                                                    .fillMaxWidth(),
                                                textAlign = TextAlign.Center
                                            )
                                        } else {
                                            Box(modifier = Modifier.fillMaxWidth()) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.lost),
                                                    contentDescription = "nameImage",
                                                    modifier = Modifier
                                                        .width(250.dp)
                                                        .height(250.dp)
                                                        .align(Alignment.Center)
                                                        .padding(5.dp, 10.dp, 5.dp, 20.dp)
                                                )
                                            }

                                            Text(
                                                text = "Better Luck Next Time!!",
                                                fontSize = 23.sp,
                                                color = Color.Black,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier = Modifier
                                                    .padding(5.dp, 10.dp)
                                                    .fillMaxWidth(),
                                                textAlign = TextAlign.Center
                                            )
                                        }

                                    }

                                    if (correctAnswers == 20) {
                                        Text(
                                            text = "Excellent $Name",
                                            fontSize = 15.sp,
                                            color = Color.Gray,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier
                                                .padding(0.dp, 0.dp)
                                                .fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )
                                    } else if (correctAnswers > 7) {
                                        Text(
                                            text = "Well done $Name",
                                            fontSize = 13.sp,
                                            color = Color.Gray,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier
                                                .padding(0.dp, 0.dp)
                                                .fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )
                                    } else {
                                        Text(
                                            text = "Try Again $Name",
                                            fontSize = 13.sp,
                                            color = Color.Gray,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier
                                                .padding(0.dp, 0.dp)
                                                .fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    Text(
                                        text = "Your Score",
                                        fontSize = 16.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier
                                            .padding(top = 40.dp)
                                            .fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )

                                    Text(
                                        text = "$correctAnswers / 20",
                                        fontSize = 35.sp,
                                        color = if (correctAnswers > 15) SpecialGreen
                                        else if (correctAnswers > 7) WarningYellow
                                        else ErrorRed,
                                        fontWeight = FontWeight.Normal,
                                        modifier = Modifier
                                            .padding(bottom = 20.dp)
                                            .fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Button(
                                        onClick = {
                                            (context as? Activity)?.finish()
                                        }, modifier =
                                        Modifier
                                            .padding(30.dp, 30.dp)
                                            .fillMaxWidth()
                                            .height(40.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = buttonBackground
                                        )
                                    ) {
                                        Text(text = "Go To Main Menu", color = Color.White)
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