package apps.szte.runningapp.screenshot

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.view.View
import com.google.android.gms.maps.GoogleMap
import java.io.FileOutputStream
import java.io.IOException


class ScreenShot {


    //static methods in Kotlin
    companion object {
        fun takeScreenShot(v: View): Bitmap {
            v.isDrawingCacheEnabled = true
            v.buildDrawingCache(true)
            var bmap = Bitmap.createBitmap(v.drawingCache)
            v.isDrawingCacheEnabled = false
            return bmap
        }

        fun takeScreenShotOfRootView(v: View): Bitmap {
            return takeScreenShot(v.rootView)
        }

        fun captureScreen(map: GoogleMap?, ctx: Context) {
            var callback = object : GoogleMap.SnapshotReadyCallback {
                override fun onSnapshotReady(p0: Bitmap?) {
                    var bitmap = p0

                    var fops: FileOutputStream? = null

                    var filePath = Environment.getExternalStorageDirectory().absolutePath + "/googleMapSnap" + ".png"

                    try {
                        fops = FileOutputStream(filePath)
                        bitmap?.compress(Bitmap.CompressFormat.PNG, 80, fops)
                        fops.flush()
                        fops.close()
                    } catch (e: IOException) {
                        //TODO: nemm üresen hagyni!!!
                    }
                }

            }
            map?.snapshot(callback)
        }

//        fun saveBitmap(b: Bitmap): File {
//            val file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RunningApp"
//            val dir = File(file_path)
//            if (!dir.exists())
//                dir.mkdirs()
//            val file = File(dir.toURI())
//            val fOut: FileOutputStream
//            try {
//                fOut = FileOutputStream(file)
//                b.compress(Bitmap.CompressFormat.PNG, 85, fOut)
//                fOut.flush()
//                fOut.close()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            return file
//        }
    }
}