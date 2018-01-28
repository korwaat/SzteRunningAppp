package apps.szte.runningapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import apps.szte.runningapp.adapter.ResultAdapter
import apps.szte.runningapp.adapter.UserAdapter
import apps.szte.runningapp.database.DbHelper
import apps.szte.runningapp.dataclasses.User
import apps.szte.runningapp.gpstracker.GpsTracker
import apps.szte.runningapp.screenshot.ScreenShot
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.share.ShareApi
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.activity_profile_screen.*
import kotlinx.android.synthetic.main.activity_results_screen.*
import kotlinx.android.synthetic.main.activity_running_screen.*
import kotlinx.android.synthetic.main.row_username.view.*
import java.text.SimpleDateFormat
import java.util.*


class ResultsActivity : AppCompatActivity() {

    private val mydb = DbHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_screen)

        val adapter = ResultAdapter(this, mydb.getUserResults((applicationContext as GlobalEnv).selectedUserName))
        resultsListView.setHasFixedSize(true)
        resultsListView.layoutManager = LinearLayoutManager(this)
        resultsListView.adapter = adapter
    }
}


class RunningActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private lateinit var startButton: Button
        private lateinit var pauseButton: Button
        private lateinit var mChrono: Chronometer
        private lateinit var gpsTracker: GpsTracker
        private lateinit var callbackManager: CallbackManager

    }

    private var mapURI: Uri? = null
    private var map: GoogleMap? = null
    private val mydb = DbHelper(this)
    private var lastPause: Long = 0
    var latlongList = mutableMapOf<Double, Double>()
    var locationList = ArrayList<LatLng>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_running_screen)
        FacebookSdk.sdkInitialize(applicationContext)

        startButton = start
        pauseButton = pause
        mChrono = chrono

        var selectedUserName = (applicationContext as GlobalEnv).selectedUserName
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.mapFragment) as SupportMapFragment
        when (!"".equals(selectedUserName)) {
            false -> {
                Toast.makeText(applicationContext, getString(R.string.noSelectProfile), Toast.LENGTH_SHORT).show()
                startButton.isEnabled = false
                pauseButton.isEnabled = false
                mapFragment.view?.visibility = View.INVISIBLE
            }
            else -> {
                gpsTracker = GpsTracker(this, this)
                mapFragment.view?.visibility = View.INVISIBLE

                val selectedUser = mydb.getUser(selectedUserName)

                val userWeight = selectedUser.weight.toInt()


                mChrono.setOnChronometerTickListener {
                    val time = SystemClock.elapsedRealtime() - mChrono.base
                    val h = (time / 3600000).toInt()
                    val m = (time - h * 3600000).toInt() / 60000
                    val s = (time - (h * 3600000).toLong() - (m * 60000).toLong()).toInt() / 1000
                    val hh = if (h < 10) "0" + h else "" + h
                    val mm = if (m < 10) "0" + m else "" + m
                    val ss = if (s < 10) "0" + s else "" + s
                    mChrono.text = "$hh:$mm:$ss"
                }

                startButton.setOnClickListener {
                    if (gpsTracker.startTracking() == 1) {
                        if (lastPause.compareTo(0) != 0) {
                            mChrono.base = mChrono.base + SystemClock.elapsedRealtime() - lastPause

                        } else {
                            mChrono.base = SystemClock.elapsedRealtime()
                        }

                        mChrono.start()
                        startButton.isEnabled = false
                        pauseButton.isEnabled = true
                    }
                }

                pauseButton.setOnClickListener {
                    lastPause = SystemClock.elapsedRealtime()
                    var base = mChrono.base
                    var systemTime = SystemClock.elapsedRealtime()
                    mChrono.stop()
                    pauseButton.isEnabled = false
                    startButton.isEnabled = true
                    gpsTracker.stopTracking()
                    var firstKey = 0.0
                    var firstValue = 0.0

                    var lastKey = 0.0
                    var lastValue = 0.0
                    var first = true
                    var resultArray = floatArrayOf(0f)
                    for ((key, value) in latlongList) {
                        if (first) {
                            firstKey = key
                            firstValue = value
                            first = false
                        }
                        var pp = LatLng(key, value)
                        locationList.add(pp)
                        lastKey = key
                        lastValue = value
                    }
                    mapFragment.getMapAsync(this)
                    mapFragment.view?.visibility = View.VISIBLE
                    Location.distanceBetween(firstKey, firstValue, lastKey, lastValue, resultArray)
                    distanceResult.text = resultArray[0].toInt().toString() + " méter"
                    var distanceDivTime = ((resultArray[0].toInt() / ((systemTime - base) * 0.001)) * 3.6).toInt()
                    avgSpeedResult.text = distanceDivTime.toString() + " Km/h"
                    var met = (3.5 * userWeight) / 200
                    var burnedKcal = ((12.5 * met) * (((systemTime - base) * 0.001) * 0.016666666666667)).toInt()
                    var currDateStamp = getCurrentTimeStamp()

                    //TODO: checkRecord sql, and toast
                    //INSERT RESULT TO DB
                    mydb.addResult((applicationContext as GlobalEnv).selectedUserName, resultArray[0].toInt(), (systemTime - base), distanceDivTime, burnedKcal, currDateStamp)

                    val imageview = screenshot

//                    var b = ScreenShot.takeScreenShotOfRootView(imageview)
                    var b = ScreenShot.takeScreenShotOfRootView(imageview)
                    var loginManager = LoginManager.getInstance()

                    shareImageBtn.setOnClickListener {
                        shareImageBtn.visibility = View.GONE
                        //Take screenshot and share it

                        callbackManager = CallbackManager.Factory.create()
                        var list = mutableListOf<String>("publish_actions")

                        loginManager.logInWithPublishPermissions(this, list)
                        var handler = object : FacebookCallback<LoginResult> {
                            override fun onSuccess(result: LoginResult?) {
                                sharePhotoToFacebook(b)
                            }

                            override fun onCancel() {
                                Toast.makeText(applicationContext, "Canceled", Toast.LENGTH_SHORT)
                            }

                            override fun onError(error: FacebookException?) {
                                Toast.makeText(applicationContext, error?.message, Toast.LENGTH_SHORT)
                            }

                        }
                        loginManager.registerCallback(callbackManager, handler)
                    }
                }
            }
        }
    }

    private fun sharePhotoToFacebook(b: Bitmap) {
        var sharePhoto = SharePhoto.Builder().setBitmap(b).setCaption("EZAZ!!!!").build()
//        var sharePhoto = SharePhoto.Builder().setImageUrl(mapURI).setCaption("EZAZ!!!!").build()

        var content = SharePhotoContent.Builder().addPhoto(sharePhoto).build()
        ShareApi.share(content, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun getCurrentTimeStamp(): String {
        var format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var currDate = format.format(Date())

        return currDate
    }


    override fun onMapReady(p0: GoogleMap) {
        if (!locationList.isEmpty()) {
            map = p0
            map?.uiSettings?.isZoomControlsEnabled = true

            var mopt1 = MarkerOptions()
            var mopt2 = MarkerOptions()

            mopt1.position(locationList.get(0)).title("Indulási pont")
            mopt2.position(locationList.get(locationList.lastIndex)).title("Befejezési pont")
            map?.addMarker(mopt1)
            map?.addMarker(mopt2)
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(locationList.get(0), 17.toFloat()))
            map?.addPolyline(PolylineOptions().clickable(false).addAll(locationList))
            ScreenShot.captureScreen(map, applicationContext)

//            var file = File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/googleMapSnap" + ".jpg")
//            mapURI = Uri.fromFile(file)
        }
    }
}


class ProfileActivity : AppCompatActivity() {

    var myDial: Dialog? = null
    private val mydb = DbHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_screen)
    }

    override fun onResume() {
        super.onResume()

        val adapter = UserAdapter(this, mydb.getUserNames())

        userNamesListView.setHasFixedSize(true)
        userNamesListView.layoutManager = LinearLayoutManager(this)
        userNamesListView.adapter = adapter
    }

    fun showPopUp(v: View) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.layout.create_profile_popup_screen)
        val myDialView = LayoutInflater.from(this).inflate(R.layout.create_profile_popup_screen, null, false)
        val picker = myDialView.findViewById<NumberPicker>(R.id.agePicker)
        val nameField = myDialView.findViewById<EditText>(R.id.profileNameField)
        val bodyWeightField = myDialView.findViewById<EditText>(R.id.bodyWeightField)
        val bodyHeightField = myDialView.findViewById<EditText>(R.id.bodyHeight)
        val okBtn = myDialView.findViewById<Button>(R.id.okButton)
        val cancelBtn = myDialView.findViewById<Button>(R.id.cancelButton)

        picker.minValue = 10
        picker.maxValue = 80

        //Listeners
        okBtn.setOnClickListener {
            val currentUser = User(nameField.text.toString(), bodyWeightField.text.toString(), picker.value, bodyHeightField.text.toString())
            mydb.addUser(currentUser)
            myDial?.cancel()
            this.onResume()
        }

        cancelBtn.setOnClickListener {
            //mydb.clearTable()
            myDial?.cancel()
            this.onResume()
        }


        builder.setView(myDialView)
        myDial = builder.create()
        myDial?.show()
        this.onPause()
    }

    fun selectUser(v: View) {
        val intent = Intent(this, MainScreen::class.java)
        (applicationContext as GlobalEnv).selectedUserName = v.userNameText.text.toString()
        startActivity(intent)
    }
}

//class WalkingActivity: AppCompatActivity(), SensorEventListener {
//
//
//    companion object {
//        private lateinit var sensorManager: SensorManager
//    }
//
//    var walking = false
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_walking_screen)
//
//        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//    }
//
//    override fun onResume() {
//        super.onResume()
//        walking = true
//        var counterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
//
//        when (counterSensor != null) {
//            true  -> sensorManager.registerListener(this, counterSensor, SensorManager.SENSOR_DELAY_UI)
//            false -> Toast.makeText(this, "A lépés számláló szenzor nem található!", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        walking = false
//        sensorManager.unregisterListener(this)
//    }
//
//
//    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
//        var a = 2
//    }
//
//    override fun onSensorChanged(p0: SensorEvent?) {
//        if (walking) {
//            stepCounter.text = p0!!.values[0].toString()
//        }
//    }
//
//}