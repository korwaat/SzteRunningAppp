package apps.szte.runningapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import apps.szte.runningapp.R
import kotlinx.android.synthetic.main.row_username.view.*


class UserAdapter(ctx: Context, list: ArrayList<String>) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {


    private var userItemList: ArrayList<String>
    private var mainCtx: Context

    init {
        userItemList = list
        mainCtx = ctx

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
                .from(mainCtx)
                .inflate(R.layout.row_username, null, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = userItemList.size //Convert extension body történt

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userNameText.text = userItemList[position] //.get helyett a tömbös indexes hivatkozás az elemre
    }

    fun addItem(item: String) {
        userItemList.add(item)
        notifyItemInserted(userItemList.lastIndex) //userItemList.size - 1
    }

    fun removeAllItems() {
        val oldSize = userItemList.size
        userItemList.clear()
        notifyItemRangeRemoved(0, oldSize)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //View.findViewByID helyett a View property-ként elérhetőek a view objektumok, automatikusan TextViewként adja vissza, castolást elvégzi
        //CTRL+Q típust kiírja
        val userNameText = itemView.userNameText
    }
}