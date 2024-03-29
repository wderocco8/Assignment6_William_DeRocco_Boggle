package com.example.assignment6_william_derocco_boggle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.assignment6_william_derocco_boggle.databinding.FragmentBoardBinding


class Board : Fragment() {
    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!

    private val tileIds = arrayOf(
        arrayOf(R.id.tile_00, R.id.tile_01, R.id.tile_02, R.id.tile_03),
        arrayOf(R.id.tile_10, R.id.tile_11, R.id.tile_12, R.id.tile_13),
        arrayOf(R.id.tile_20, R.id.tile_21, R.id.tile_22, R.id.tile_23),
        arrayOf(R.id.tile_30, R.id.tile_31, R.id.tile_32, R.id.tile_33),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Call newGame() function when the view is created
        newGame()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun newGame() {
        // Access the tile TextViews using binding
        for (i in tileIds.indices) {
            for (j in 0 until tileIds[i].size) {
                val tileId = tileIds[i][j]
                val textView = binding.root.findViewById<TextView>(tileId)
                // reset tile text to a random letter
                textView.text = getRandomLetter()
            }
        }
    }

    private fun getRandomLetter(): String {
        val alphabet = ('A'..'Z').toList()
        return alphabet.random().toString()
    }
}


