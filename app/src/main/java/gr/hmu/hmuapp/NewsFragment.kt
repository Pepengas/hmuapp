package gr.hmu.hmuapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import gr.hmu.hmuapp.data.fetchRss
import gr.hmu.hmuapp.databinding.FragmentNewsBinding
import kotlinx.coroutines.launch

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = NewsAdapter { item ->
            val bundle = Bundle().apply { putString("url", item.link) }
            findNavController().navigate(R.id.webViewFragment, bundle)
        }
        binding.newsList.layoutManager = LinearLayoutManager(requireContext())
        binding.newsList.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { loadNews() }
        loadNews()
    }

    private fun loadNews() {
        binding.swipeRefresh.isRefreshing = true
        if (!hasInternetConnection()) {
            binding.swipeRefresh.isRefreshing = false
            Toast.makeText(requireContext(), R.string.no_internet, Toast.LENGTH_LONG).show()
            return
        }
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val items = fetchRss("https://ee.hmu.gr/feed/")
                adapter.submitList(items)
            } catch (e: Exception) {
                Log.e("NewsFragment", "Failed to load news", e)
                Toast.makeText(requireContext(), R.string.fetch_failed, Toast.LENGTH_LONG).show()
            } finally {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = cm.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val networkInfo = cm.activeNetworkInfo ?: return false
            networkInfo.isConnected
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
