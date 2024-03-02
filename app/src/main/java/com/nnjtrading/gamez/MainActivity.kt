package com.nnjtrading.gamez

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
import com.nnjtrading.gamez.openActivity as openActivity1


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
                    MainLayout()
                }
            }

        }
    }

}

private fun openActivity(type: String, context: Context) {

    var intent: Intent = Intent(context, IntroductionActivity::class.java)
    intent.putExtra("type", type);
    context.startActivity(intent)
}

private fun getGameFirstTime(context: Context, type: String): String? {
    val sharedPref = context.getSharedPreferences("NamePreference", Context.MODE_PRIVATE)
    return sharedPref.getString(type, null)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainLayout() {

    val context = LocalContext.current

    GamezTheme {
        val gradient = Brush.linearGradient(
            0.0f to Color(0xff1F1147),
            10.0f to Color(0xFF361E70),
            start = Offset(0f,0f),
            end = Offset(0f,1500f)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(gradient)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp)) {

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
                    ), modifier = Modifier.padding(0.dp, 20.dp, 0.dp, 20.dp), fontFamily = FontFamily.Monospace)

                }

                Column(verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {

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
}