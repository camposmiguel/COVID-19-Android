package es.thegoodcode.covid.ui.radar

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.thegoodcode.covid.R


class ConnectionsFragment : Fragment() {

    private var columnCount = 1
    private lateinit var connectionsAdapter: MyConnectionsRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_connections_list, container, false)

        connectionsAdapter = MyConnectionsRecyclerViewAdapter()

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = connectionsAdapter
            }
        }
        return view
    }

}
