/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.left_right_blink.presentation


import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.example.left_right_blink.R
import com.example.left_right_blink.presentation.theme.Left_right_blinkTheme
import java.util.Arrays


class MainActivity : ComponentActivity(), SensorEventListener  {
    private lateinit var mSensorManager : SensorManager
    private var mAccelerometer : Sensor ?= null
    private var mLightSensor : Sensor ?= null
    private var resume = false;
    private var counter = 0;
    private var state = 0;
    private val dark_values = mutableListOf<Int>()
    private val bright_values = mutableListOf<Int>()


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            if (event.sensor.type == Sensor.TYPE_LIGHT) {


                setContentView(R.layout.activity_main)

                val textView = TextView(this)

                textView.setText(event.values[0].toString());


                if(counter % 15 > 3){
                    Log.d("light", event.values[0].toString())
                }
                counter += 1
                if (counter >= 15){
                    val params = window.attributes
                    params.screenBrightness = 1f
                    window.attributes = params

                    setContentView(R.layout.activity_main)

                    val imageView = ImageView(this)
                    // setting height and width of imageview
                    imageView.layoutParams= LinearLayout.LayoutParams(400, 400)
                    //imageView.x= 20F // setting margin from left
                    //imageView.y= 20F // setting margin from top

                    // accessing our custom image which we added in drawable folder
                    if(state==0){
                    val imgResId = R.drawable.upper_right_1
                    var resId = imgResId
                    imageView.setImageResource(resId)
                    // accessing our relative layout from activity_main.xml
                    val layout = findViewById<RelativeLayout>(R.id.layout)
                        layout?.addView(imageView) // adding image to the layout
                    }
                    else{val imgResId = R.drawable.lower_left_1
                        var resId = imgResId
                        imageView.setImageResource(resId)
                        // accessing our relative layout from activity_main.xml
                        val layout = findViewById<RelativeLayout>(R.id.layout)
                        layout?.addView(imageView) // adding image to the layout
                    }

                    // Add ImageView to LinearLayout



                    //

                    if(counter%15>3){
                        bright_values.add(event.values[0].toInt())
                    }
                    if(counter == 30){
                        bright_values.sort()
                        val median_bright = bright_values.get((bright_values.size / 2).toInt())
                        dark_values.sort()
                        val median_dark = dark_values.get((dark_values.size / 2).toInt())
                        if(median_bright - median_dark > 60)
                            Log.d("cover_output", ("Covered").toString())
                        else
                            Log.d("cover_output", ("Not Covered").toString())
                        Log.d("light_delta", (median_bright - median_dark).toString())
                        state = 1-state;

                        counter = 0
                        dark_values.clear()
                        bright_values.clear()

                    }
                }
                else{
                    val params = window.attributes
                    params.screenBrightness = 0f
                    window.attributes = params
                    if(counter%15>3){
                        dark_values.add(event.values[0].toInt())
                    }

                }
            }
        }
    }



    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager.registerListener(this, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp("Android")
        }
    }
}

@Composable
fun WearApp(greetingName: String) {
    Left_right_blinkTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            Greeting(greetingName = greetingName)
        }
    }
}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android")
}


