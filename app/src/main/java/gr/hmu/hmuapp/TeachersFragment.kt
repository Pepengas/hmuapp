package gr.hmu.hmuapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import gr.hmu.hmuapp.data.fetchTeachers
import gr.hmu.hmuapp.data.Teacher
import gr.hmu.hmuapp.databinding.FragmentTeachersBinding
import kotlinx.coroutines.launch

class TeachersFragment : Fragment() {

    private var _binding: FragmentTeachersBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TeachersAdapter
    private var allTeachers: List<Teacher> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeachersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TeachersAdapter { teacher ->
            if (teacher.profileUrl.isNotBlank()) {
                val bundle = Bundle().apply { putString("url", teacher.profileUrl) }
                findNavController().navigate(R.id.webViewFragment, bundle)
            }
        }
        binding.teacherList.layoutManager = LinearLayoutManager(requireContext())
        binding.teacherList.adapter = adapter

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true
            }
        })

        loadTeachers()
    }

    private fun filter(query: String?) {
        val filtered = if (query.isNullOrBlank()) {
            allTeachers
        } else {
            val q = query.lowercase()
            allTeachers.filter { it.name.lowercase().contains(q) }
        }
        adapter.submitList(filtered)
    }

    private fun loadTeachers() {
        viewLifecycleOwner.lifecycleScope.launch {
            val teachers = fetchTeachers()
            allTeachers = teachers
            adapter.submitList(teachers)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
