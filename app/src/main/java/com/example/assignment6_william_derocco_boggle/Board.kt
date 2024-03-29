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

    // 2-d array of all tile values
    private val tileIds = arrayOf(
        arrayOf(R.id.tile_00, R.id.tile_01, R.id.tile_02, R.id.tile_03),
        arrayOf(R.id.tile_10, R.id.tile_11, R.id.tile_12, R.id.tile_13),
        arrayOf(R.id.tile_20, R.id.tile_21, R.id.tile_22, R.id.tile_23),
        arrayOf(R.id.tile_30, R.id.tile_31, R.id.tile_32, R.id.tile_33),
    )

    // Set to store valid clickable tile IDs
    private val clickableTileIds = mutableSetOf<Int>()
    // set to store all clicked IDs in the current word
    private val clickedIds = mutableSetOf<Int>()

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

        // clear clickable ids
        clickableTileIds.clear()

        // Access the tile TextViews using binding
        for (i in tileIds.indices) {
            for (j in 0 until tileIds[i].size) {
                val tileId = tileIds[i][j]
                val textView = binding.root.findViewById<TextView>(tileId)

                // reset tile text to a random letter
                textView.text = getRandomLetter()

                // make every tile clickable at first
                clickableTileIds.add(tileId)

                // add onClick listener to update game state
                textView.setOnClickListener {
                    if (clickableTileIds.contains(tileId)) {
                        // Update currentWord TextView with the clicked tile's letter
                        binding.currentWord.append(textView.text)

                        // add tileId to set of clicked ids
                        clickedIds.add(tileId)

                        // Disable click listeners for non-adjacent tiles
                        updateClickableTileIds(i, j)
                    }
                }

            }
        }
    }

    private fun getRandomLetter(): String {
        val alphabet = ('A'..'Z').toList()
        return alphabet.random().toString()
    }

    // Function to populate clickableTileIds with adjacent tiles
    private fun populateClickableTileIds(rowIndex: Int, colIndex: Int) {
        clickableTileIds.clear()
        // Add adjacent tiles to clickableTileIds
        val adjacentIndices = arrayOf(
            Pair(rowIndex - 1, colIndex), // above
            Pair(rowIndex + 1, colIndex), // below
            Pair(rowIndex, colIndex - 1), // left
            Pair(rowIndex, colIndex + 1), // right
            Pair(rowIndex - 1, colIndex - 1), // top left
            Pair(rowIndex - 1, colIndex + 1), // top right
            Pair(rowIndex + 1, colIndex - 1), // bottom left
            Pair(rowIndex + 1, colIndex + 1) // bottom right
        )
        for ((row, col) in adjacentIndices) {
            // if valid index AND tileId is not in clickedIds
            if (row in tileIds.indices &&
                col in tileIds[row].indices &&
                !clickedIds.contains(tileIds[row][col])) {
                // make tile clickable
                clickableTileIds.add(tileIds[row][col])
            }
        }
    }

    // Function to update clickableTileIds after a tile is clicked
    private fun updateClickableTileIds(rowIndex: Int, colIndex: Int) {
        // Populate clickableTileIds with adjacent tiles of the clicked tile
        populateClickableTileIds(rowIndex, colIndex)
    }
}


