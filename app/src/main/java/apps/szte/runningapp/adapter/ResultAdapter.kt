package apps.szte.runningapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import apps.szte.runningapp.R
import apps.szte.runningapp.dataclasses.ResultItem
import kotlinx.android.synthetic.main.row_results.view.*


class ResultAdapter(ctx: Context, list: ArrayList<ResultItem>) : RecyclerView.Adapter<ResultAdapter.ViewHolder>() {

    private var resultItemList: ArrayList<ResultItem>
    private var mainCtx: Context

    init {
        resultItemList = list
        mainCtx = ctx

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultAdapter.ViewHolder {
        val view = LayoutInflater
                .from(mainCtx)
                .inflate(R.layout.row_results, null, false)

        return ResultAdapter.ViewHolder(view)
    }

    override fun getItemCount() = resultItemList.size

    override fun onBindViewHolder(holder: ResultAdapter.ViewHolder, position: Int) {
        //.get helyett a tömbös indexes hivatkozás az elemre
        holder.distance.text = resultItemList[position].distance + " méter"
        holder.date.text = resultItemList[position].date
        holder.avgSpeed.text = resultItemList[position].avgSpeed + " km/h"
        holder.burnedKcal.text = resultItemList[position].burnedKcal + " kCal"

        val time = resultItemList[position].overallTime.toLong()
        val h = (time / 3600000).toInt()
        val m = (time - h * 3600000).toInt() / 60000
        val s = (time - (h * 3600000).toLong() - (m * 60000).toLong()).toInt() / 1000
        val hh = if (h < 10) "0" + h else "" + h
        val mm = if (m < 10) "0" + m else "" + m
        val ss = if (s < 10) "0" + s else "" + s
        holder.overallTime.text = ("$hh:$mm:$ss")

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //View.findViewByID helyett a View property-ként elérhetőek a view objektumok, automatikusan TextViewként adja vissza, castolást elvégzi
        //CTRL+Q típust kiírja
        val distance = itemView.distanceText
        val date = itemView.dateText
        val avgSpeed = itemView.avgSpeedText
        val burnedKcal = itemView.burnedKcalText
        val overallTime = itemView.overallTimeText


    }

}