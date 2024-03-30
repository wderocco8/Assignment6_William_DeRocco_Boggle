
package com.example.assignment6_william_derocco_boggle

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.assignment6_william_derocco_boggle.databinding.FragmentGameStateBinding

class GameState : Fragment() {

    private var _binding: FragmentGameStateBinding? = null
    private val binding get() = _binding!!
    interface NewGameListener {
        fun onNewGameClicked()
    }

    private var newGameListener: NewGameListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameStateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set OnClickListener to the NewGame button
        binding.newGameButton.setOnClickListener {
            newGameListener?.onNewGameClicked()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NewGameListener) {
            newGameListener = context
        } else {
            throw RuntimeException("$context must implement NewGameListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        newGameListener = null
    }

    // function to update the scoreText TextView
    fun updateScore(score: Int) {
        // Check if the binding is null
        if (_binding == null) {
            return
        }

        // if negative score, set to 0 (otherwise, set directly
        if (score < 0) {
            binding.scoreText.text = "0"
        } else {
            binding.scoreText.text = score.toString()
        }
    }

}