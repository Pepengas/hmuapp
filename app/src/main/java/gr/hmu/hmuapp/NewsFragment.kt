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

        viewLifecycleOwner.lifecycleScope.launch {
             try {
                val items = fetchRss("https://ee.hmu.gr/feed/")
                adapter.submitList(items)
            } catch (e: Exception) {
                Log.e("NewsFragment", "Failed to load news", e)
                Toast.makeText(requireContext(), R.string.fetch_failed, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
