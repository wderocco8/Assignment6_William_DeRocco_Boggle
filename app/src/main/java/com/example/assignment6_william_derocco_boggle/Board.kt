package com.example.assignment6_william_derocco_boggle

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.assignment6_william_derocco_boggle.databinding.FragmentBoardBinding
import kotlinx.coroutines.currentCoroutineContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.math.max


class Board : Fragment() {
    // interface to see if submit button has been clicked
    interface SubmitListener {
        fun onSubmitClicked(newScore: Int)
    }
    private var submitListener: SubmitListener? = null

    // use ViewBinding for easier access to elements
    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!

    // 2-d array of all tile values
    private val tileIds = arrayOf(
        arrayOf(R.id.tile_00, R.id.tile_01, R.id.tile_02, R.id.tile_03),
        arrayOf(R.id.tile_10, R.id.tile_11, R.id.tile_12, R.id.tile_13),
        arrayOf(R.id.tile_20, R.id.tile_21, R.id.tile_22, R.id.tile_23),
        arrayOf(R.id.tile_30, R.id.tile_31, R.id.tile_32, R.id.tile_33),
    )

    // list of vowels and special consonants
    private val vowels = "aeiou"
    private val specials = "szpxq"

    // Set to store valid clickable tile IDs
    private val clickableTileIds = mutableSetOf<Int>()
    // set to store all clicked IDs in the current word
    private val clickedIds = mutableSetOf<Int>()
    // set to store all clicked IDs in the SUBMITTED words
    private val submittedIds = mutableSetOf<Int>()
    // set of all valid words
    private val validWords = HashSet<String>()
    // initialize user's score to 0
    private var score = 0


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

        // create button listener for clearing a word
        binding.clearButton.setOnClickListener {
            clearWord()
        }

        // Load valid words when the fragment is created
        loadValidWords()

        // Create button listener for submitting a word
        binding.submitButton.setOnClickListener {
            checkWordValidity(binding.currentWord.text.toString())
        }
    }

    private fun loadValidWords() {
        try {
            // Open the file containing valid words
            val inputStream: InputStream = resources.openRawResource(R.raw.words)

            // Create a reader to read the file
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            // Read each line and add it to the set of valid words
            while (reader.readLine().also { line = it } != null) {
                validWords.add(line!!.trim().lowercase())
            }
            // Close the reader
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun checkWordValidity(word: String) {
        val wordLower = word.lowercase()
        // Check if the word exists in the set of valid words
        if (wordLower.length < 4) {
            // Word doesn't contain 2 vowels
            showToast("Invalid word: $wordLower (must contain at least 4 letters)")
        } else if (!hasTwoVowels(wordLower)) {
            // Word doesn't contain 2 vowels
            showToast("Invalid word: $wordLower (must contain at least 2 vowels)")
        } else if (!validWords.contains(wordLower)) {
            // Word is not valid, handle accordingly
            showToast("Invalid word -10: $wordLower (not in dictionary)")

            // deduct 10 points if arrived here
            score = max(score - 10, 0)

            // clear the current word
            clearWord()

            // call GameState to update score
            submitListener?.onSubmitClicked(score)
        } else {
            // Word is VALID, handle accordingly
            // score the word and update GameState fragment
            scoreWord(word)
        }
    }

    private fun scoreWord(word: String) {
        // keep track of if score should be doubled
        var double = false

        // iterate over each letter in the word
        for (letter in word) {
            when (letter) {
                in vowels -> {
                    // vowels count as 5pts
                    score += 5
                }
                in specials -> {
                    // found special consonant -> double score
                    double = true
                    score++
                }
                else -> {
                    // any other consonant
                    score++
                }
            }
        }

        if (double) {
            score *= 2
        }

        // Add clicked IDs to submitted IDs
        clickedIds.forEach { submittedIds.add(it) }

        // clear the current word
        clearWord()

        showToast("Valid word +$score: $word")
        // call GameState to update score
        submitListener?.onSubmitClicked(score)
    }

    private fun hasTwoVowels(word: String): Boolean {
        // ensure word has at least two vowels
        var vowelCount = 0

        for (char in word) {
            if (char in vowels) {
                vowelCount++
                if (vowelCount >= 2) {
                    return true
                }
            }
        }

        return false
    }

    fun newGame() {
        // clear clickableTileIds
        clickableTileIds.clear()

        // clear clickedIds
        clickedIds.clear()

        // clear submitted ids
        submittedIds.clear()

        // get rid of any text
        binding.currentWord.text = ""

        // reset score
        score = 0

        // call GameState to update score
        submitListener?.onSubmitClicked(score)

        // Access the tile TextViews using binding
        for (i in tileIds.indices) {
            for (j in 0 until tileIds[i].size) {
                val tileId = tileIds[i][j]
                val textView = binding.root.findViewById<TextView>(tileId)

                // reset tile text to a random letter
                textView.text = getRandomLetter()

                // make every tile clickable at first
                clickableTileIds.add(tileId)

                // update color to activeTile status
                textView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.activeTile))

                // add onClick listener to update game state
                textView.setOnClickListener {
                    // tile cannot be submitted already AND must be adjacent
                    if (!submittedIds.contains(tileId) && clickableTileIds.contains(tileId)) {
                        // Update currentWord TextView with the clicked tile's letter
                        binding.currentWord.append(textView.text)

                        // add tileId to set of clicked ids
                        clickedIds.add(tileId)

                        // update color to inactiveTile status
                        textView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.inactiveTile))

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

    // Function to clear the current word
    private fun clearWord() {
        // clear clickableTileIds
        clickableTileIds.clear()

        // clear clickedIds
        clickedIds.clear()

        // get rid of any text
        binding.currentWord.text = ""

        // Access the tile TextViews using binding and RESET color
        for (i in tileIds.indices) {
            for (j in 0 until tileIds[i].size) {
                val tileId = tileIds[i][j]

                // only make clickable if not in submittedIds
                if (!submittedIds.contains(tileId)) {
                    val textView = binding.root.findViewById<TextView>(tileId)

                    // update color to activeTile status
                    textView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.activeTile))

                    // re-add clickable Id
                    clickableTileIds.add(tileId)
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SubmitListener) {
            submitListener = context
        } else {
            throw RuntimeException("$context must implement NewGameListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        submitListener = null
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


