package com.arnis.dh.ui.main

import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import com.arnis.dh.R
import com.arnis.dh.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class MainFragment : Fragment(), KoinComponent {

    private val viewModel by inject<MainViewModel>()
    private var recyclerState: Parcelable? = null
    private val adapter = ItemAdapter { viewModel.preview(it) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        savedInstanceState?.getParcelable<Parcelable>(RECYCLER_STATE)?.let {
            recyclerState = it
        }
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainRecycler.adapter = adapter
        val span = if (view.context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            1
        else
            2
        mainRecycler.layoutManager = GridLayoutManager(context, span)
        mainSwipeRefresh.setOnRefreshListener {
            viewModel.updateData()
        }
        viewModel.getRefreshing().observe(this) {
            mainSwipeRefresh.isRefreshing = it
        }
        viewModel.getItems().observe(this) {
            adapter.submitList(it)
            restoreRecyclerState()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mainRecycler?.layoutManager?.let {
            outState.putParcelable(RECYCLER_STATE, it.onSaveInstanceState())
        }
    }

    private inline fun restoreRecyclerState() {
        recyclerState?.let {
            mainRecycler.layoutManager?.onRestoreInstanceState(it)
            recyclerState = null
        }
    }

    companion object {
        private const val RECYCLER_STATE = "rec_state"
    }
}
