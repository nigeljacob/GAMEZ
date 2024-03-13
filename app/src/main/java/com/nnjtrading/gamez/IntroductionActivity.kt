package com.nnjtrading.gamez

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nnjtrading.gamez.ui.theme.GamezTheme

class IntroductionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GamezTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ProvideIntent(intent = intent) {
                        IntroductionLayout()
                    }
                }
            }
        }
    }
}

fun getUserName(context: Context): String? {
    val sharedPref = context.getSharedPreferences("NamePreference", Context.MODE_PRIVATE)
    return sharedPref.getString("UserName", null)
}

fun setUserName(context: Context, name: String?) {
    val sharedPreference =  context.getSharedPreferences("NamePreference", Context.MODE_PRIVATE)
    var editor = sharedPreference.edit()
    editor.putString("UserName", name)
    editor.commit()
}

private fun openActivity(type: String?, context: Context) {

    if(type.equals("Guess The Country")) {
        var intent: Intent = Intent(context, GuessTheCountry::class.java)
        context.startActivity(intent)
    } else if(type.equals("Guess Hints")) {
        var intent: Intent = Intent(context, GuessHints::class.java)
        context.startActivity(intent)
    } else if(type.equals("Guess The Flag")) {
        var intent: Intent = Intent(context, GuessTheFlag::class.java)
        context.startActivity(intent)
    } else {
        var intent: Intent = Intent(context, Advanced::class.java)
        context.startActivity(intent)
    }

    (context as? Activity)?.finish()
}

private val LocalCurrentIntent = staticCompositionLocalOf<Intent?> { null }

// Function to set the current Intent
@Composable
fun ProvideIntent(intent: Intent?, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalCurrentIntent provides intent) {
        content()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun IntroductionLayout() {

    var context = LocalContext.current
//    setUserName(context, null)

    val intent = LocalCurrentIntent.current
    val Type = intent?.getStringExtra("type")

    var userName by remember { mutableStateOf(getUserName(context)) }
    var greeting = "Hello"

    var isLoading by remember { mutableStateOf(true) }

    if(userName != null) {
        greeting = "Welcome Back"
    }

    GamezTheme {
        val gradient = Brush.linearGradient(
            0.0f to Color(0xff1F1147),
            10.0f to Color(0xFF361E70),
            start = Offset(0f,0f),
            end = Offset(0f,1500f)
        )

        Column( verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(gradient)) {

            Text(text = "Before we get started", fontSize = 15.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.ExtraBold, color = Color(
                0xFF3ce9bb
            ), modifier = Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp), fontFamily = FontFamily.Monospace)

            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(30.dp, 30.dp)
                    .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
                    .background(Color.White)) {


                if(isLoading) {
                    Column(verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()) {
                        CircularProgressIndicator(
                            modifier = Modifier.width(54.dp),
                            color = Color(0xFF3ce9bb)
                        )

                        Handler().postDelayed(
                            {
                                isLoading = false
                            }, 1000)
                    }
                } else if(userName == null) {

                    var userNameState by remember { mutableStateOf("") }

                    Column(horizontalAlignment = Alignment.CenterHorizontally,) {

                        Text(text = "Tell us More about yourself", color = Color.Black ,fontSize = 20.sp, modifier = Modifier.padding(0.dp, 20.dp))

                        Image(painter = painterResource(id = R.drawable.man), contentDescription = "nameImage", modifier = Modifier
                            .width(250.dp)
                            .height(250.dp)
                            .padding(0.dp, 20.dp, 0.dp, 0.dp))

                        TextField(
                            value = userNameState,
                            placeholder = { Text(text = "name")},
                            onValueChange = { if (it.length <= 30) userNameState = it},
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp, 40.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color(0xffE7E7E7),
                                focusedIndicatorColor = Color.Transparent,
                                placeholderColor = Color.Gray,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = Color.Black,
                                textColor = Color.Black))

                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxHeight()) {
                            Button(
                                onClick =  {
                                    if(userNameState.equals("")) {
                                        Toast.makeText(context, "Fill in the name field to continue", Toast.LENGTH_SHORT).show()
                                    } else {
                                        userName = userNameState
                                        setUserName(context, userName!!)
                                    }
                                }, modifier = Modifier
                                    .padding(30.dp, 30.dp)
                                    .fillMaxWidth()
                                    .height(40.dp), colors = ButtonDefaults.buttonColors(backgroundColor = Color(
                                    0xFF6949FD
                                ))) {
                                Text(text = "Continue" , color = Color.White)
                            }
                        }

                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Text(
                            text = "$greeting $userName !",
                            fontSize = 18.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(20.dp, 10.dp)
                        )

                        Text(
                            text = "This is $Type",
                            fontSize = 20.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(0.dp, 20.dp)
                        )

                        Image(painter = painterResource(id = R.drawable.question), contentDescription = "nameImage", modifier = Modifier
                            .width(250.dp)
                            .height(250.dp)
                            .padding(0.dp, 10.dp, 0.dp, 0.dp))

                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp, 0.dp), horizontalAlignment = Alignment.CenterHorizontally) {

                            if(Type.equals("Guess The Country")) {

                                Text(text = "Instructions to Play Game", color = Color.Black ,modifier = Modifier.padding(0.dp, 10.dp), fontWeight = FontWeight.Bold)
                                Text(text = "1. A flag pointing to a country will be displayed", color = Color.Gray ,modifier = Modifier.fillMaxWidth().padding(0.dp, 5.dp), textAlign = TextAlign.Left);
                                Text(text = "2. You have to select the correct country pointing to the flag from the given list", color = Color.Gray ,modifier = Modifier.fillMaxWidth().padding(0.dp, 5.dp), textAlign = TextAlign.Left);
                                Text(text = "3. There will be a total of 20 flags displayed, the more you guess the more you gain points", color = Color.Gray , modifier = Modifier.fillMaxWidth().padding(0.dp, 5.dp), textAlign = TextAlign.Left);


                            } else if(Type.equals("Guess Hints")) {

                                Text(text = "Instructions to Play Game", color = Color.Black ,modifier = Modifier.padding(0.dp, 10.dp), fontWeight = FontWeight.Bold)
                                Text(text = "1. A flag pointing to a country will be displayed along with a few blank spaces", color = Color.Gray ,modifier = Modifier.fillMaxWidth().padding(0.dp, 5.dp), textAlign = TextAlign.Left);
                                Text(text = "2. Type a single character in the text box to guess a letter of the country's name.", color = Color.Gray ,modifier = Modifier.fillMaxWidth().padding(0.dp, 5.dp), textAlign = TextAlign.Left);
                                Text(text = "3. Click \"Submit\" to check your guess - correct guesses will replace corresponding dashes.", color = Color.Gray , modifier = Modifier.fillMaxWidth().padding(0.dp, 5.dp), textAlign = TextAlign.Left);

                            } else if(Type.equals("Guess The Flag")) {
                                Text(text = "Instructions to Play Game", color = Color.Black ,modifier = Modifier.padding(0.dp, 10.dp), fontWeight = FontWeight.Bold)
                                Text(text = "1. A Country Name along with three random flag images will be displayed", color = Color.Gray ,modifier = Modifier.fillMaxWidth().padding(0.dp, 5.dp), textAlign = TextAlign.Left);
                                Text(text = "2. You have to select the correct flag that points to the displayed name", color = Color.Gray ,modifier = Modifier.fillMaxWidth().padding(0.dp, 5.dp), textAlign = TextAlign.Left);
                                Text(text = "3. Click \"Submit\" to check your guess", color = Color.Gray , modifier = Modifier.fillMaxWidth().padding(0.dp, 5.dp), textAlign = TextAlign.Left);

                            } else {
                                Text(text = "Instructions to Play Game", color = Color.Black ,modifier = Modifier.padding(0.dp, 10.dp), fontWeight = FontWeight.Bold)
                                Text(text = "1. A Country Name along with three random flag images will be displayed", color = Color.Gray ,modifier = Modifier.fillMaxWidth().padding(0.dp, 5.dp), textAlign = TextAlign.Left);
                                Text(text = "2. You have to select the correct flag that points to the displayed name", color = Color.Gray ,modifier = Modifier.fillMaxWidth().padding(0.dp, 5.dp), textAlign = TextAlign.Left);
                                Text(text = "3. Click \"Submit\" to check your guess", color = Color.Gray , modifier = Modifier.fillMaxWidth().padding(0.dp, 5.dp), textAlign = TextAlign.Left);

                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxHeight()) {
                                Button(
                                    onClick =  {
                                        openActivity(Type, context)
                                    }, modifier = Modifier
                                        .padding(30.dp, 30.dp)
                                        .fillMaxWidth()
                                        .height(40.dp), colors = ButtonDefaults.buttonColors(backgroundColor = Color(
                                        0xFF6949FD
                                    ))) {
                                    Text(text = "Play Game" , color = Color.White)
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}