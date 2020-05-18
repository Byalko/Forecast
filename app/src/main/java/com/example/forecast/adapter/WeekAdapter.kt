import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.forecast.R
import com.example.forecast.model.WeatherList
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row.view.*

class WeekAdapter(private val weath: List<WeatherList>)
    : RecyclerView.Adapter<WeekAdapter.ViewHolder>(){

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView= LayoutInflater.from(parent.context)
            .inflate(R.layout.row,parent,false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = weath.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = weath[position]
        with(holder) {
            view.TimeItem.text=post.dt_txt
            view.DesItem.text=post.weather[0].description
            view.DegreeItem.text=post.main.temp.toInt().toString()+"°C"

            val url= "http://openweathermap.org/img/wn/"+ "${weath[position].weather[0].icon}" +"@2x.png"
            Picasso.get()
                .load(url)
                .into(holder.view.ImageItem)
        }
    }
}