package com.nnjtrading.gamez

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nnjtrading.gamez.ui.theme.*
import java.util.*

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
}

fun generateRandomFlag(context: Context): Question {

    val countries = Countries(context)

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

fun generateRandomFlagOptions(context: Context, question: String): MutableList<String> {
    val countries = Countries(context)
    val countryCodesMap = countries.countriesCodeList()

    var generatedCodes = mutableListOf<String>()

    var RandomNumberGenerator = Random()

    var count  = 0;

    while(count < 2) {
        var randomNumber = RandomNumberGenerator.nextInt(255);
        var flag = countryCodesMap.get(randomNumber)

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

            generatedCodes.add(flag.toLowerCase())
            count++
        }
    }

    return generatedCodes
}
fun getCountryFlagResourceId(context: Context, countryCode: String,): Int {
    return context.resources.getIdentifier(countryCode, "drawable", context.packageName)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GuessTheFlagLayout() {

    val context = LocalContext.current

    var question by rememberSaveable { mutableStateOf(generateRandomFlag(context)) }

    var flag by rememberSaveable { mutableStateOf(question.getFlag().toLowerCase()) }

    var generatedRandomFlags by rememberSaveable { mutableStateOf(generateRandomFlagOptions(context, flag))}

    var RandomNumberGenerator = Random()

    var randomNumber by rememberSaveable { mutableStateOf(RandomNumberGenerator.nextInt(3))}

    generatedRandomFlags.add(randomNumber, flag)

    var flagResourceId by rememberSaveable { mutableStateOf(mutableListOf<Int>())}

    for(flagIndex in generatedRandomFlags) {
        flagResourceId.add(getCountryFlagResourceId(context, flagIndex))
    }

    var completedQuestions by  rememberSaveable { mutableStateOf(1) }

    var correctAnswers by rememberSaveable { mutableStateOf(0) }

    var selected by rememberSaveable { mutableStateOf(-1) }

    var didWin by rememberSaveable { mutableStateOf(correctAnswers > 15) }

    var isLoading by rememberSaveable { mutableStateOf(true) }

    var answered by rememberSaveable { mutableStateOf(false) }

    var isCorrect by rememberSaveable { mutableStateOf(false) };

    var correctAnswer by rememberSaveable { mutableStateOf(randomNumber + 1) };

    var Name = getUserName(context)

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

                                    Text(text = "$questionCountry", modifier = Modifier.fillMaxWidth()                                          .align(Alignment.Center)
                                            .padding(5.dp, 5.dp, 5.dp, 5.dp),
                                        fontSize = 26.sp, textAlign = TextAlign.Center
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
                                                    text = "Option $correctAnswer",
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

                            var items by rememberSaveable {
                                mutableStateOf(
                                    flagResourceId.map {
                                        AnswerOptionLayout(isSelected = false, title = "$it")
                                    }
                                )
                            }

                            LazyColumn(modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp, 10.dp, 20.dp, 10.dp)
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
                                }) {

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
                                        .clickable {

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
                                        .clip(RoundedCornerShape(size = 5.dp))
                                        .background(
                                            if (items.get(i).isSelected) SpecialGreen
                                            else Color.Gray.copy(0.08f)
                                        )) {

                                        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxSize().weight(1f)) {
                                            Image(
                                                painter = painterResource(id = flagResourceId.get(i)),
                                                contentDescription = "nameImage",
                                                modifier = Modifier
                                                    .width(150.dp)
                                                    .height(150.dp)
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

                                        if (answered) {
                                            if (selected != -1) {

                                                completedQuestions++

                                                if (completedQuestions < 21) {
                                                    isLoading = true
                                                    answered = false
                                                    isCorrect = false
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
                                                }

                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Select a Flag to submit answer",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } else {

                                            if (selected != -1) {
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

