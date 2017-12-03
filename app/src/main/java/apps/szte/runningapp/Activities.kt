package apps.szte.runningapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_running_screen.*

/**
 * Created by Kokaa on 2017. 11. 26..
 * Outer Class for activities.
 */

class Activities : AppCompatActivity() {

    class RunningActivity: AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_running_screen)

            startPauseButton.setOnClickListener {
                when (stopper.stopperIsRunning) {
                    true  -> stopStopper()
                    false -> startStopper()
                }
            }
        }

        private fun stopStopper() {
            stopper.stop()
            startPauseButton.text = getString(R.string.StartPauseButtonContent02)
        }

        private fun startStopper() {
            stopper.start()
            startPauseButton.text = getString(R.string.StartPauseButtonContent01)
        }

        override fun onPause() {
            super.onPause()
            stopper.stop()
            stopper.stoppedByOnPause = true
        }

        override fun onResume() {
            super.onResume()
            when (stopper.stoppedByOnPause) {
                true -> stopper.start()
            }
        }

    }

    class WalkingActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            //TODO .....
        }

    }
}