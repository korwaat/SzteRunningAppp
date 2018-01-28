package apps.szte.runningapp.gpstracker

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import apps.szte.runningapp.R
import apps.szte.runningapp.RunningActivity


class GpsTracker(context: Context, runAct: RunningActivity) : LocationListener {

    private lateinit var locm: LocationManager
    private var ctx: Context
    private val runact: RunningActivity

    init {
        ctx = context
        runact = runAct
    }


    fun stopTracking() {
        locm.removeUpdates(this)
    }


    fun startTracking(): Int {
        locm = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        //Getting GPS status
        if (locm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            try {
                locm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 7F, this)
            } catch (ex: SecurityException) {
                Toast.makeText(ctx, ctx.getString(R.string.errorTryItAgain), Toast.LENGTH_SHORT).show()
                Log.e("SECUTIRY", ex.message)
                return 0
            }
        } else {
            Toast.makeText(ctx, ctx.getString(R.string.plsEnableGPS), Toast.LENGTH_SHORT).show()
            return 0
        }
        return 1 //Return error, or non error code. 1 - non error code, 0 - error code
    }

    override fun onLocationChanged(p0: Location) {
        runact.latlongList.put(p0.latitude, p0.longitude)
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        Log.d("SAJAT", p0)
    }

    override fun onProviderEnabled(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}