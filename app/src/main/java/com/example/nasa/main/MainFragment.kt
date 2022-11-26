package com.example.nasa.main

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.nasa.R
import com.example.nasa.databinding.FragmentMainBinding
import com.example.nasa.network.AsteroidApiFilter
import timber.log.Timber

@Suppress("DEPRECATION")
class MainFragment : Fragment() {

    private val viewModel: ViewModel by lazy {
        val activity = requireNotNull(this.activity) {}
        ViewModelProvider(
            this,
            ViewModelFactory(activity.application)
        )[ViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = DataBindingUtil.inflate<FragmentMainBinding>(
            inflater,
            R.layout.fragment_main,
            container,
            false
        )
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.asteroidRecycler.adapter = CustomAdapter(CustomAdapter.OnClickListener {
            Timber.i("Asteroid clicked Complete: %s", it.codename)
            viewModel.displayAsteroidDetails(it)
        })

        viewModel.navigateToSelectedProperty.observe(viewLifecycleOwner) {
            if (null != it) {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.displayAsteroidDetailsComplete()
            }
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.updateFilter(
            when (item.itemId) {
                R.id.show_week_menu -> AsteroidApiFilter.WEEK
                R.id.show_today_menu -> AsteroidApiFilter.TODAY
                else -> AsteroidApiFilter.SAVED
            }
        )
        return true
    }
}

