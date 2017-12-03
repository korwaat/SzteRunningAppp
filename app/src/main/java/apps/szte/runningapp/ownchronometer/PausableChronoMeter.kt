package apps.szte.runningapp.ownchronometer

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import android.widget.Chronometer
import kotlinx.android.synthetic.main.activity_running_screen.view.*

/**
 * Created by Kokaa on 2017. 11. 26..
 */


class PausableChronoMeter : Chronometer {

    constructor(ctx : Context) : super(ctx)
    constructor(ctx : Context, attrs : AttributeSet) : super(ctx, attrs)
    constructor(ctx : Context, attrs : AttributeSet, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr)
    constructor(ctx : Context, attrs : AttributeSet, defStyleAttr: Int, defStyleRes : Int) : super(ctx, attrs, defStyleAttr, defStyleRes)

    var timeWhenStopped : Long = 0
    var stopperIsRunning : Boolean = false
    var stoppedByOnPause = false

    override fun start() {
           stopperIsRunning = true
           base = SystemClock.elapsedRealtime() + timeWhenStopped
           super.start()
    }

    override fun stop() {
          stopperIsRunning = false
          super.stop()
          timeWhenStopped = base - SystemClock.elapsedRealtime()
    }
}