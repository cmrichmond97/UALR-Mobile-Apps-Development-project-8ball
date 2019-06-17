package edu.ualr.cmrichmond.magic8ball


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import java.lang.Math.abs
import java.util.Random


class MainActivity : AppCompatActivity(), SensorEventListener {
    // We need a SensorManager and a Sensor to get readings
    // from the accelerometer


    private  var mySensorManager: SensorManager? = null
    private var myAccelerometer: Sensor? = null

    //variables for tracking shakes
    private  var lastX:Float = 0f//last known X position
    private var lastY: Float = 0f//last known Y position
    private var lastZ: Float = 0f//last known Z position
    private  var lastTime: Long = 0//time of last check

    //strings holding the responses to be given on shake

    private val responseOne:String = ("Try Again Later")
    private val responseTwo:String = ("Go Left")
    private val responseThree:String = ("Look Up")
    private val responseFour:String = ("Yes")
    private val responseFive:String = ("Roll Initiative")
    private val responseSix:String = ("Help!  I'm Trapped In This Phone!")
    private  val responseSeven:String = ("No")
    private val responseEight:String = ("Maybe")
    private val responseNine:String = ("You Can Certainly Try")
    private val responseTen:String = ("Possibly")

    // array to hold the responses
    private val responses = arrayOf(responseOne, responseTwo, responseThree, responseFour, responseFive, responseSix, responseSeven, responseEight,
            responseNine, responseTen)

    private var theAnswer:Int = 0

    // reference to the messageText textView
    private val messageText:TextView = findViewById(R.id.messageText)

    // random range function
    private fun ClosedRange<Int>.random() =
                    Random().nextInt((endInclusive + 1) - start) +  start


    override fun onCreate(savedInstanceState: Bundle?) {//this function sets up the sensormanager and the sensor
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // We need to set up our SensorManager and Sensor in
        // onCreate().  Note that our View will not start getting
        // readings from the accelerometer until we run the
        // registerListener() method on our SensorManager
        mySensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        myAccelerometer = mySensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onResume() {
        super.onResume()

        // This function call will let our SensorManager begin receiving
        // readings from our accelerometer
        mySensorManager!!.registerListener(this, myAccelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()

        // This function call releases the accelerometer to save
        // battery life.  When our app isn't in the foreground, we
        // don't need to continue receiving updates from the accelerometer
        mySensorManager!!.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // We have to implement this method because SensorEventListener
        // is an abstract class, but we don't need to be worried about
        // the accuracy changing on the accelerometer
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // This is the function that will be updated every time our
        // accelerometer sends an update to our apps

        //get current time from system
        val currentTime = System.currentTimeMillis()

        //initialize variables to hold current X,Y,and Z coords

        val timeChange = currentTime-lastTime

        //print values for debugging
        Log.d("UALR_UTIL", "X Reading: " + event!!.values[0].toString())
        Log.d("UALR_UTIL", "Y Reading: " + event!!.values[1].toString())
        Log.d("UALR_UTIL", "Z Reading: " + event!!.values[2].toString())
        Log.d("UALR_UTIL", "Last time: " + lastTime.toString())
        Log.d("UALR_UTIL", "current time: " + currentTime.toString())
        Log.d("UALR_UTIL", "time change: " + timeChange.toString())


        if(timeChange>5)
        {
            //update x,y, and z from sensors
            val x = event!!.values[0]
            val y = event!!.values[1]
            val z = event!!.values[2]

            //turn current and last known positions into single values for easier comparison
            val part1 = x+y+z
            val part2 = lastX+lastY+lastZ

            //find current speed of phone
            val changeInPosition = abs(part1-part2)
            val changeInTime = currentTime-lastTime
            val speed = changeInPosition/changeInTime

            //print value for debugging
            Log.d("UALR_UTIL", "speed: " + speed.toString())

            if(speed>.13)//if speed is greater than the threshold for a shake
            {
                //print a random message to the screen
                theAnswer = (0..9).random()

                messageText.text = (responses[theAnswer])

            }
            //record current X, Y, Z, and time as last known
            lastX = x
            lastY = y
            lastZ = z
            lastTime = currentTime
        }


    }

    override fun onSaveInstanceState(savedInstanceState: Bundle?) {
        super.onSaveInstanceState(savedInstanceState)

        //save the current message
        savedInstanceState!!.putInt("currentText", theAnswer)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        //restore the saved message
        theAnswer = savedInstanceState!!.getInt("currentText")
        messageText.text = responses[theAnswer]
    }
}

